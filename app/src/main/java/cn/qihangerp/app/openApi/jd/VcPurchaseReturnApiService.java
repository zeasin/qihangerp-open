package cn.qihangerp.app.openApi.jd;//package cn.qihangerp.jd.openApi;
//
//import cn.qihangerp.common.ResultVo;
//import cn.qihangerp.common.ResultVoEnum;
//import cn.qihangerp.common.enums.EnumShopType;
//import cn.qihangerp.common.mq.MqMessage;
//import cn.qihangerp.common.mq.MqType;
//import cn.qihangerp.common.mq.MqUtils;
//import cn.qihangerp.open.jd.domain.JdVcReturn;
//import cn.qihangerp.open.jd.domain.SysShopPullLasttime;
//import cn.qihangerp.open.jd.domain.SysShopPullLogs;
//import cn.qihangerp.jd.service.JdVcReturnService;
//import cn.qihangerp.jd.service.SysShopPullLasttimeService;
//import cn.qihangerp.jd.service.SysShopPullLogsService;
//import lombok.AllArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//
//@AllArgsConstructor
//@Service
//public class VcPurchaseReturnApiService {
//    private final SysShopPullLasttimeService pullLasttimeService;
//    private final SysShopPullLogsService pullLogsService;
//    private final JdVcReturnService jdVcReturnService;
//    private final MqUtils mqUtils;
//    public void pullVcReturnList(String pullWay,Integer shopId,String sellerId,String serverUrl,String appKey,String appSecret,String accessToken){
//
//        Date currDateTime = new Date();
//        long beginTime = System.currentTimeMillis();
//
//        // 获取最后更新时间
//        LocalDateTime startTime = null;
//        LocalDateTime endTime = null;
//        SysShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(shopId, "REFUND");
//        if (lasttime == null) {
//            endTime = LocalDateTime.now();
//            startTime = endTime.minusDays(1);
//        } else {
//            startTime = lasttime.getLasttime().minusHours(1);//取上次结束一个小时前
//            Duration duration = Duration.between(startTime, LocalDateTime.now());
//            long hours = duration.toHours();
//            if (hours > 24) {
//                // 大于24小时，只取24小时
//                endTime = startTime.plusHours(24);
//            } else {
//                endTime = LocalDateTime.now();
//            }
////            endTime = startTime.plusDays(1);//取24小时
////            if (endTime.isAfter(LocalDateTime.now())) {
////                endTime = LocalDateTime.now();
////            }
//        }
//
//        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//
//        int insertSuccess = 0;//新增成功的订单
//        int totalError = 0;
//        int hasExist = 0;//已存在的订单数
//        ResultVo<JdVcReturn> resultVo = VcPurchaseReturnApiHelper.pullReturn(startTime, endTime, serverUrl, appKey, appSecret, accessToken);
//        if (resultVo.getCode() == 0) {
//
//            for (var jdVcReturn : resultVo.getList()) {
//
//                var result = jdVcReturnService.saveReturn(shopId, jdVcReturn);
//                if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
//                    //已经存在
//                    hasExist++;
//                    mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, jdVcReturn.getReturnId().toString()));
//                } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
//                    insertSuccess++;
//                    mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, jdVcReturn.getReturnId().toString()));
//                } else {
//                    totalError++;
//                }
//            }
//
//            if (lasttime == null) {
//                // 新增
//                SysShopPullLasttime insertLasttime = new SysShopPullLasttime();
//                insertLasttime.setShopId(shopId);
//                insertLasttime.setCreateTime(new Date());
//                insertLasttime.setLasttime(endTime);
//                insertLasttime.setPullType("REFUND");
//                pullLasttimeService.save(insertLasttime);
//
//            } else {
//                // 修改
//                SysShopPullLasttime updateLasttime = new SysShopPullLasttime();
//                updateLasttime.setId(lasttime.getId());
//                updateLasttime.setUpdateTime(new Date());
//                updateLasttime.setLasttime(endTime);
//                pullLasttimeService.updateById(updateLasttime);
//            }
//            SysShopPullLogs logs = new SysShopPullLogs();
//            logs.setShopId(shopId);
//            logs.setShopType(EnumShopType.JDVC.getIndex());
//            logs.setPullType("REFUND");
//            logs.setPullWay(pullWay);
//            logs.setPullParams("{CreateDateBegin:" + startTimeStr + ",CreateDateEnd:" + endTimeStr + ",PageIndex:1,PageSize:50}");
//            logs.setPullResult("{total:" + insertSuccess + ",hasExist:" + hasExist + ",totalError:" + totalError + "}");
//            logs.setPullTime(currDateTime);
//            logs.setDuration(System.currentTimeMillis() - beginTime);
//            pullLogsService.save(logs);
//        }
//    }
//}
