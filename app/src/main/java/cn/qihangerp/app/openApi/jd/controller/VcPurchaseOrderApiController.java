package cn.qihangerp.app.openApi.jd.controller;//package cn.qihangerp.jd.controller.vc;
//
//import cn.qihangerp.common.AjaxResult;
//import cn.qihangerp.common.ResultVo;
//import cn.qihangerp.common.ResultVoEnum;
//import cn.qihangerp.common.enums.EnumShopType;
//import cn.qihangerp.common.enums.HttpStatus;
//import cn.qihangerp.common.mq.MqMessage;
//import cn.qihangerp.common.mq.MqType;
//import cn.qihangerp.common.mq.MqUtils;
//import cn.qihangerp.open.jd.domain.*;
//import cn.qihangerp.jd.openApi.*;
//import cn.qihangerp.open.jd.service.*;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//
//@RequestMapping("/vc/order")
//@RestController
//@AllArgsConstructor
//public class VcPurchaseOrderApiController {
//    private final ApiCommon apiCommon;
////    private final RedisCache redisCache;
//    private final MqUtils mqUtils;
//    private final JdOrderService orderService;
//    private final JdVcPurchaseOrderService jdVcPurchaseOrderService;
//    private final SysShopPullLasttimeService pullLasttimeService;
//    private final SysShopPullLogsService pullLogsService;
//
//    @RequestMapping(value = "/pull_order", method = RequestMethod.POST)
//    public AjaxResult pullList(@RequestBody PullRequest params) throws Exception {
//        if (params.getShopId() == null || params.getShopId() <= 0) {
//            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
//        }
//
//        Date currDateTime = new Date();
//        long beginTime = System.currentTimeMillis();
//
//        var checkResult = apiCommon.checkBefore(params.getShopId());
//        if (checkResult.getCode() != HttpStatus.SUCCESS) {
//            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(),checkResult.getData());
//        }
//        String accessToken = checkResult.getData().getAccessToken();
//        String serverUrl = checkResult.getData().getServerUrl();
//        String appKey = checkResult.getData().getAppKey();
//        String appSecret = checkResult.getData().getAppSecret();
//
//        // 获取最后更新时间
//        LocalDateTime startTime = null;
//        LocalDateTime  endTime = null;
//        SysShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(params.getShopId(), "ORDER");
//        if(lasttime == null){
//            endTime = LocalDateTime.now();
//            startTime = endTime.minusDays(1);
//        }else{
//            startTime = lasttime.getLasttime().minusHours(1);//取上次结束一个小时前
//            Duration duration = Duration.between(startTime, LocalDateTime.now());
//            long hours = duration.toHours();
////            if (hours > 24) {
////                // 大于24小时，只取24小时
////                endTime = startTime.plusHours(24);
////            } else {
////                endTime = LocalDateTime.now();
////            }
//            if(hours<24){
//                // 时间相差24小时的话
//                startTime.plusHours(24-hours);
//                endTime = LocalDateTime.now();
//            } else {
//                endTime = LocalDateTime.now();
//            }
//        }
//
//
//        //拉取订单
//        ResultVo<JdVcPurchaseOrder> upResult = VcPurchaseOrderApiHelper.pullOrder(startTime,endTime,serverUrl,appKey,appSecret,accessToken);
//        int insertSuccess = 0;//新增成功的订单
//        int totalError = 0;
//        int hasExistOrder = 0;//已存在的订单数
//        //循环插入订单数据到数据库
//        for (var order : upResult.getList()) {
//            //插入订单数据
//            var result = jdVcPurchaseOrderService.saveOrder(params.getShopId().longValue(), order);
//            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
//                //已经存在
//                hasExistOrder++;
//                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC,MqType.ORDER_MESSAGE,order.getOrderId().toString()));
//            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
//                insertSuccess++;
//                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC,MqType.ORDER_MESSAGE,order.getOrderId().toString()));
//            } else {
//                totalError++;
//            }
//        }
//        if(lasttime == null){
//            // 新增
//            SysShopPullLasttime insertLasttime = new SysShopPullLasttime();
//            insertLasttime.setShopId(params.getShopId());
//            insertLasttime.setCreateTime(new Date());
//            insertLasttime.setLasttime(endTime);
//            insertLasttime.setPullType("ORDER");
//            pullLasttimeService.save(insertLasttime);
//
//        }else {
//            // 修改
//            SysShopPullLasttime updateLasttime = new SysShopPullLasttime();
//            updateLasttime.setId(lasttime.getId());
//            updateLasttime.setUpdateTime(new Date());
//            updateLasttime.setLasttime(endTime);
//            pullLasttimeService.updateById(updateLasttime);
//        }
//        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        String paramsStr = "{startTime:"+startTime.format(df)+",endTime:"+endTime.format(df)+"}";
//        String resultStr ="{insertSuccess:"+insertSuccess+",hasExistOrder:"+hasExistOrder+",totalError:"+totalError+"}";
//
//        SysShopPullLogs logs = new SysShopPullLogs();
//        logs.setShopType(EnumShopType.JDVC.getIndex());
//        logs.setShopId(params.getShopId());
//        logs.setPullType("ORDER");
//        logs.setPullWay("主动拉取");
//        logs.setPullParams(paramsStr);
//        logs.setPullResult(resultStr);
//        logs.setPullTime(currDateTime);
//        logs.setDuration(System.currentTimeMillis() - beginTime);
//        pullLogsService.save(logs);
//        String msg = "成功{startTime:"+startTime.format(df)+",endTime:"+endTime.format(df)+"}总共找到：" + upResult.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
//        return AjaxResult.success(msg);
//    }
//
//
//}
//
//
