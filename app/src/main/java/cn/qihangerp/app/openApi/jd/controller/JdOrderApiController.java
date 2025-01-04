package cn.qihangerp.app.openApi.jd.controller;

import cn.qihangerp.app.openApi.jd.JdApiCommon;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.common.utils.StringUtils;
import cn.qihangerp.domain.OShopPullLasttime;
import cn.qihangerp.domain.OShopPullLogs;
import cn.qihangerp.module.service.OShopPullLasttimeService;
import cn.qihangerp.module.service.OShopPullLogsService;

import cn.qihangerp.sdk.common.ApiResultVo;
import cn.qihangerp.sdk.jd.OrderApiHelper;
import cn.qihangerp.sdk.jd.PullRequest;
import cn.qihangerp.module.open.jd.domain.JdOrder;
import cn.qihangerp.module.open.jd.domain.JdOrderItem;
import cn.qihangerp.sdk.jd.response.JdOrderResponse;
import cn.qihangerp.module.open.jd.service.JdOrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping("/api/open-api/jd/order")
@RestController
@AllArgsConstructor
public class JdOrderApiController {
    private final JdApiCommon jdApiCommon;
//    private final RedisCache redisCache;
    private final MqUtils mqUtils;
    private final JdOrderService orderService;
    private final OShopPullLasttimeService pullLasttimeService;
    private final OShopPullLogsService pullLogsService;

    @RequestMapping(value = "/pull_order_jd", method = RequestMethod.POST)
    public AjaxResult pullList(@RequestBody PullRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }

        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();

        var checkResult = jdApiCommon.checkBefore(params.getShopId());
        if (checkResult.getCode() != HttpStatus.SUCCESS) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(),checkResult.getData());
        }
        String accessToken = checkResult.getData().getAccessToken();
        String serverUrl = checkResult.getData().getServerUrl();
        String appKey = checkResult.getData().getAppKey();
        String appSecret = checkResult.getData().getAppSecret();

        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime  endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(params.getShopId(), "ORDER");
        if(lasttime == null){
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(1);
        }else{
            startTime = lasttime.getLasttime().minusHours(1);//取上次结束一个小时前
            Duration duration = Duration.between(startTime, LocalDateTime.now());
            long hours = duration.toHours();
            if (hours > 24) {
                // 大于24小时，只取24小时
                endTime = startTime.plusHours(24);
            } else {
                endTime = LocalDateTime.now();
            }
//            endTime = startTime.plusDays(1);//取24小时
//            if(endTime.isAfter(LocalDateTime.now())){
//                endTime = LocalDateTime.now();
//            }
        }

        //第一次获取
        ApiResultVo<JdOrderResponse> upResult = OrderApiHelper.pullOrder(startTime,endTime,serverUrl,appKey,appSecret,accessToken);
        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExistOrder = 0;//已存在的订单数
        //循环插入订单数据到数据库
        for (var order : upResult.getList()) {
            JdOrder jdOrder = new JdOrder();
            BeanUtils.copyProperties(order,jdOrder);
            List<JdOrderItem> jdOrderItemList = new ArrayList<>();
            if(order.getItems()!=null && order.getItems().size()>0){
                for (var item:order.getItems()){
                    JdOrderItem jdOrderItem = new JdOrderItem();
                    BeanUtils.copyProperties(item,jdOrderItem);
                    jdOrderItemList.add(jdOrderItem);
                }
            }
            jdOrder.setItems(jdOrderItemList);
            //插入订单数据
            var result = orderService.saveOrder(params.getShopId(), jdOrder);
            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                //已经存在
                hasExistOrder++;
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD,MqType.ORDER_MESSAGE,order.getOrderId()));
            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                insertSuccess++;
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD,MqType.ORDER_MESSAGE,order.getOrderId()));
            } else {
                totalError++;
            }
        }
        if(totalError==0) {
            if (lasttime == null) {
                // 新增
                OShopPullLasttime insertLasttime = new OShopPullLasttime();
                insertLasttime.setShopId(params.getShopId());
                insertLasttime.setCreateTime(new Date());
                insertLasttime.setLasttime(endTime);
                insertLasttime.setPullType("ORDER");
                pullLasttimeService.save(insertLasttime);

            } else {
                // 修改
                OShopPullLasttime updateLasttime = new OShopPullLasttime();
                updateLasttime.setId(lasttime.getId());
                updateLasttime.setUpdateTime(new Date());
                updateLasttime.setLasttime(endTime);
                pullLasttimeService.updateById(updateLasttime);
            }
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        String paramsStr = "{startTime:"+startTime.format(df)+",endTime:"+endTime.format(df)+"}";
        String resultStr ="{insertSuccess:"+insertSuccess+",hasExistOrder:"+hasExistOrder+",totalError:"+totalError+"}";

        OShopPullLogs logs = new OShopPullLogs();
        logs.setShopType(EnumShopType.JD.getIndex());
        logs.setShopId(params.getShopId());
        logs.setPullType("ORDER");
        logs.setPullWay("主动拉取");
        logs.setPullParams(paramsStr);
        logs.setPullResult(resultStr);
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - beginTime);
        pullLogsService.save(logs);
        String msg = "成功{startTime:"+startTime.format(df)+",endTime:"+endTime.format(df)+"}总共找到：" + upResult.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
        return AjaxResult.success(msg);
    }

    /**
     * 拉取详情
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_order_detail", method = RequestMethod.POST)
    public AjaxResult pullDetail(@RequestBody PullRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        if (StringUtils.isEmpty(params.getOrderId())) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，缺少orderId");
        }
        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();

        var checkResult = jdApiCommon.checkBefore(params.getShopId());
        if (checkResult.getCode() != HttpStatus.SUCCESS) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(),checkResult.getData());
        }
        String accessToken = checkResult.getData().getAccessToken();
        String serverUrl = checkResult.getData().getServerUrl();
        String appKey = checkResult.getData().getAppKey();
        String appSecret = checkResult.getData().getAppSecret();
        ApiResultVo<JdOrderResponse> upResult = OrderApiHelper.pullOrderDetail(Long.parseLong(params.getOrderId()),serverUrl,appKey,appSecret,accessToken);
        if(upResult.getCode() == ResultVoEnum.SUCCESS.getIndex()){
            JdOrder jdOrder = new JdOrder();
            BeanUtils.copyProperties(jdOrder,upResult.getData());
            List<JdOrderItem> jdOrderItemList = new ArrayList<>();
            if(upResult.getData().getItems()!=null && upResult.getData().getItems().size()>0){
                for (var item:upResult.getData().getItems()){
                    JdOrderItem jdOrderItem = new JdOrderItem();
                    BeanUtils.copyProperties(item,jdOrderItem);
                    jdOrderItemList.add(jdOrderItem);
                }
            }
            jdOrder.setItems(jdOrderItemList);
            // 更新Order
            var result = orderService.saveOrder(params.getShopId(), jdOrder);
            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                //已经存在
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD,MqType.ORDER_MESSAGE,upResult.getData().getOrderId()));
            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD,MqType.ORDER_MESSAGE,upResult.getData().getOrderId()));
            }
            return AjaxResult.success();
        }else{
            return AjaxResult.error();
        }
    }
}


