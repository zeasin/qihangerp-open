package cn.qihangerp.app.openApi.jd;//package cn.qihangerp.jd.openApi;
//
//import com.jd.open.api.sdk.DefaultJdClient;
//import com.jd.open.api.sdk.JdClient;
//import com.jd.open.api.sdk.request.shangjiashouhou.AscQueryListRequest;
//import com.jd.open.api.sdk.response.shangjiashouhou.AscQueryListResponse;
//import cn.qihangerp.common.AjaxResult;
//import cn.qihangerp.common.ResultVoEnum;
//import cn.qihangerp.common.enums.EnumShopType;
//import cn.qihangerp.common.enums.HttpStatus;
//import cn.qihangerp.common.mq.MqMessage;
//import cn.qihangerp.common.mq.MqType;
//import cn.qihangerp.common.mq.MqUtils;
//import cn.qihangerp.open.jd.domain.SysShopPullLasttime;
//import cn.qihangerp.open.jd.domain.SysShopPullLogs;
//import cn.qihangerp.jd.service.JdOrderService;
//import cn.qihangerp.jd.service.SysShopPullLasttimeService;
//import cn.qihangerp.jd.service.SysShopPullLogsService;
//import lombok.AllArgsConstructor;
//import org.springframework.beans.BeanUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//
//@AllArgsConstructor
//@Service
//public class RefundApiService {
//    private final SysShopPullLasttimeService pullLasttimeService;
//    private final SysShopPullLogsService pullLogsService;
////    private final JdOrderAfterService afterService;
//    private final MqUtils mqUtils;
//    public void pullRefund(String pullWay,Integer shopId,String sellerId,String serverUrl,String appKey,String appSecret,String accessToken)  {
//        Date currDateTime = new Date();
//        long beginTime = System.currentTimeMillis();
//        // 获取最后更新时间
//        LocalDateTime startTime = null;
//        LocalDateTime endTime = null;
//        SysShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(shopId, "REFUND");
//        if (lasttime == null) {
//            endTime = LocalDateTime.now();
//            startTime = endTime.minusDays(1);
//        } else {
//            startTime = lasttime.getLasttime().minusHours(1);//取上次结束一个小时前
//            endTime = startTime.plusDays(1);//取24小时
//            if (endTime.isAfter(LocalDateTime.now())) {
//                endTime = LocalDateTime.now();
//            }
//        }
//
//        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        JdClient client = new DefaultJdClient(serverUrl, accessToken, appKey, appSecret);
//        try {
//            // 用于更新状态
//            // https://open.jd.com/home/home/#/doc/api?apiCateId=241&apiId=2171&apiName=jingdong.asc.sync.list
//
//
//            // https://open.jd.com/home/home/#/doc/api?apiCateId=241&apiId=2136&apiName=jingdong.asc.query.list
//            AscQueryListRequest request = new AscQueryListRequest();
//            request.setBuId(sellerId);
//            request.setOperatePin("testPin");
//            request.setOperateNick("testPin");
//            request.setApplyTimeBegin(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
//            request.setApplyTimeEnd(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
//            request.setPageNumber(1);
//            request.setPageSize(100);
//            AscQueryListResponse response = client.execute(request);
//            int insertSuccess = 0;
//            int totalError = 0;
//            int hasExist = 0;
//            if (response != null && response.getPageResult() != null) {
//                if (response.getPageResult().getData() != null) {
//                    for (var item : response.getPageResult().getData()) {
////                        JdOrderAfter after = new JdOrderAfter();
////                        BeanUtils.copyProperties(item, after);
////                    // 详情 https://open.jd.com/home/home/#/doc/api?apiCateId=241&apiId=2118&apiName=jingdong.asc.query.view
////                    AscQueryViewRequest request2=new AscQueryViewRequest();
////                    request2.setBuId("10706");
////                    request2.setOperatePin("testPin");
////                    request2.setOperateNick("testPin");
////                    request2.setServiceId(item.getServiceId());
////                    request2.setOrderId(item.getOrderId());
////                    AscQueryViewResponse response2=client.execute(request2);
////                        after.setShopId(shopId);
////                        var result = afterService.saveAfter(shopId, after);
////                        if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
////                            //已经存在
////                            hasExist++;
////                            mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE, item.getServiceId().toString()));
////                        } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
////                            insertSuccess++;
////                            mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE, item.getServiceId().toString()));
////                        } else {
////                            totalError++;
////                        }
//                    }
//                }
//            }
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
//            logs.setShopType(EnumShopType.JD.getIndex());
//            logs.setPullType("REFUND");
//            logs.setPullWay(pullWay);
//            logs.setPullParams("{ApplyTimeBegin:" + startTimeStr + ",ApplyTimeEnd:" + endTimeStr + ",PageIndex:1,PageSize:100}");
//            logs.setPullResult("{total:" + insertSuccess + ",hasExist:" + hasExist + ",totalError:" + totalError + "}");
//            logs.setPullTime(currDateTime);
//            logs.setDuration(System.currentTimeMillis() - beginTime);
//            pullLogsService.save(logs);
//        }catch (Exception e){
//            pullLogsService.save(new SysShopPullLogs(shopId, EnumShopType.JD.getIndex(), "REFUND", pullWay, "", e.getMessage(), currDateTime, System.currentTimeMillis() - beginTime));
//        }
//
//
//
//    }
//}
