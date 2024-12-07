package cn.qihangerp.app.openApi.pdd;

import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.domain.OShopPullLasttime;
import cn.qihangerp.domain.OShopPullLogs;
import cn.qihangerp.module.service.OShopPullLasttimeService;
import cn.qihangerp.module.service.OShopPullLogsService;
import cn.qihangerp.sdk.common.ApiResultVo;
import cn.qihangerp.sdk.pdd.OrderApiHelper;
import cn.qihangerp.open.pdd.domain.PddOrder;
import cn.qihangerp.sdk.pdd.response.PddOrderResponse;
import cn.qihangerp.open.pdd.service.PddOrderService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Log
@AllArgsConstructor
@Service
public class PddOrderApiService {

    private final PddOrderService orderService;
    private final MqUtils mqUtils;
    private final OShopPullLogsService pullLogsService;
    private final OShopPullLasttimeService pullLasttimeService;


    public void pullOrder(String pullWay,Long shopId,String appKey,String appSecret,String accessToken) throws Exception {
        log.info("/**************增量拉取pdd订单****************/"+pullWay);

        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();

        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime  endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(shopId, "ORDER");
        if(lasttime == null){
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(1);
        }else {
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
        String pullParams = "{startTime:"+startTime+",endTime:"+endTime+"}";
        //获取
        ApiResultVo<PddOrderResponse> upResult = OrderApiHelper.pullOrderList(appKey, appSecret, accessToken,startTime, endTime);


        if(upResult.getCode() !=0 ){
            OShopPullLogs logs = new OShopPullLogs();
            logs.setShopId(shopId);
            logs.setShopType(EnumShopType.DOU.getIndex());
            logs.setPullType("ORDER");
            logs.setPullWay(pullWay);
            logs.setPullParams(pullParams);
            logs.setPullResult(upResult.getMsg());
            logs.setPullTime(currDateTime);
            logs.setDuration(System.currentTimeMillis() - beginTime);
            pullLogsService.save(logs);
            return ;
        }



        log.info("/**************主动更新pdd订单：获取结果：总记录数" + upResult.getTotalRecords() + "****************/"+pullWay);
        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExistOrder = 0;//已存在的订单数

        //循环插入订单数据到数据库
        for (var order : upResult.getList()) {
            PddOrder pddOrder = new PddOrder();
            BeanUtils.copyProperties(order,pddOrder);
            //插入订单数据
            var result = orderService.saveOrder(shopId, pddOrder);
            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                //已经存在
                log.info("/**************主动更新pdd订单：开始更新数据库：" + order.getOrderSn() + "存在、更新************开始通知****/"+pullWay);
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.PDD, MqType.ORDER_MESSAGE,order.getOrderSn()));
                hasExistOrder++;
            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                log.info("/**************主动更新pdd订单：开始更新数据库：" + order.getOrderSn() + "不存在、新增************开始通知****/"+pullWay);
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.PDD,MqType.ORDER_MESSAGE,order.getOrderSn()));
                insertSuccess++;
            } else {
                log.info("/**************主动更新pdd订单：开始更新数据库：" + order.getOrderSn() + "报错****************/"+pullWay);
                totalError++;
            }
        }

        if(lasttime == null){
            // 新增
            OShopPullLasttime insertLasttime = new OShopPullLasttime();
            insertLasttime.setShopId(shopId);
            insertLasttime.setCreateTime(new Date());
            insertLasttime.setLasttime(endTime);
            insertLasttime.setPullType("ORDER");
            pullLasttimeService.save(insertLasttime);

        }else {
            // 修改
            OShopPullLasttime updateLasttime = new OShopPullLasttime();
            updateLasttime.setId(lasttime.getId());
            updateLasttime.setUpdateTime(new Date());
            updateLasttime.setLasttime(endTime);
            pullLasttimeService.updateById(updateLasttime);
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        OShopPullLogs logs = new OShopPullLogs();
        logs.setShopType(EnumShopType.PDD.getIndex());
        logs.setShopId(shopId);
        logs.setPullType("ORDER");
        logs.setPullWay(pullWay);
        logs.setPullParams(pullParams);
        logs.setPullResult("{insert:"+insertSuccess+",update:"+hasExistOrder+",fail:"+totalError+"}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - beginTime);
        pullLogsService.save(logs);

        String msg = "成功{startTime:"+startTime.format(df)+",endTime:"+endTime.format(df)+"}总共找到：" + upResult.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
        log.info("/**************主动更新pdd订单：END：" + msg + "****************/"+pullWay);
    }
}
