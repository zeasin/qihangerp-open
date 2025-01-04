package cn.qihangerp.app.openApi.jd;

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
import cn.qihangerp.sdk.jd.AfterSaleApiHelper;
import cn.qihangerp.module.open.jd.domain.JdRefund;
import cn.qihangerp.sdk.jd.response.JdRefundResponse;
import cn.qihangerp.module.open.jd.service.JdRefundService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class JdAfterSaleApiService {
    private final OShopPullLasttimeService pullLasttimeService;
    private final OShopPullLogsService pullLogsService;
    private final JdRefundService afterSaleService;
    private final MqUtils mqUtils;

    /**
     * 拉取售后列表（退款、售后）
     * @param pullWay
     * @param shopId
     * @param sellerId
     * @param serverUrl
     * @param appKey
     * @param appSecret
     * @param accessToken
     * @return
     * @throws Exception
     */
    public void pullAfterSaleList(String pullWay,Long shopId,Long sellerId,String serverUrl,String appKey,String appSecret,String accessToken) throws Exception {
        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();

        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(shopId, "REFUND");
        if (lasttime == null) {
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(1);
        } else {
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
//            if (endTime.isAfter(LocalDateTime.now())) {
//                endTime = LocalDateTime.now();
//            }
        }

        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //获取退款
        ApiResultVo<JdRefundResponse> refundVo = AfterSaleApiHelper.pullOrderCancel(startTime,endTime,serverUrl,appKey,appSecret,accessToken);
        //获取售后
        ApiResultVo<JdRefundResponse> afterSaleVo = AfterSaleApiHelper.pullServiceList(sellerId,startTime,endTime,serverUrl,appKey,appSecret,accessToken);
        // 合并两个列表
        List<JdRefundResponse> mergedList = Stream.of(refundVo.getList(), afterSaleVo.getList())
                .flatMap(List::stream)
                .collect(Collectors.toList());

//        mergedList.sort(Comparator.comparing(JdAfterSale::getApplyTime));

        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExist = 0;//已存在的订单数
        //循环插入订单数据到数据库
        for (var after : mergedList) {
            after.setShopId(shopId);
            JdRefund jdRefund = new JdRefund();
            BeanUtils.copyProperties(after,jdRefund);
            jdRefund.setShopId(shopId);
            var result = afterSaleService.saveRefund(shopId, jdRefund);
            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                //已经存在
                hasExist++;
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE, after.getServiceId().toString()));
            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                insertSuccess++;
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE, after.getServiceId().toString()));
            } else {
                totalError++;
            }
        }

       /* JdClient client = new DefaultJdClient(serverUrl, accessToken, appKey, appSecret);

        // 拉取退款
        PopAfsSoaRefundapplyQueryPageListRequest request1=new PopAfsSoaRefundapplyQueryPageListRequest();
//        request.setIds("1,1");
//        request.setStatus(0);
//        request.setOrderId("1234567890");
//        request.setBuyerId("aaa");
//        request.setBuyerName("aaa");
//        request1.setApplyTimeStart(startTimeStr);
//        request1.setApplyTimeEnd(endTimeStr);
//        request.setCheckTimeStart("2013-08-06 21:02:07");
//        request.setCheckTimeEnd("2013-08-06 21:02:07");
        request1.setPageIndex(1);
        request1.setPageSize(50);
//        request.setStoreId(0);
//        request.setShowSku(false);
        PopAfsSoaRefundapplyQueryPageListResponse response1=client.execute(request1);

        PopAfsSoaRefundapplyQueryByIdRequest requestDetail=new PopAfsSoaRefundapplyQueryByIdRequest();
        requestDetail.setId(23798816970L);
        PopAfsSoaRefundapplyQueryByIdResponse responseDetail=client.execute(requestDetail);
        //  查看售后和退款信息
        // https://open.jd.com/#/doc/api?apiCateId=241&apiId=3405&apiName=jingdong.asc.serviceAndRefund.view
        *//*AscServiceAndRefundViewRequest requestList=new AscServiceAndRefundViewRequest();
        requestList.setOrderId(291184764662L);
//        requestList.setApplyTimeBegin(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
//        requestList.setApplyTimeEnd(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
//        requestList.setApproveTimeBegin(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
//        requestList.setApproveTimeEnd(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));


//        requestList.setApproveTimeBegin(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
//        requestList.setApproveTimeEnd(new Date());
//        request.setApproveTimeBegin(new Date());
//        request.setApproveTimeEnd(new Date());
        requestList.setPageNumber(1);
        requestList.setPageSize(100);
//        request.setExtJsonStr("abc");
        requestList.setBuId(sellerId);
        AscServiceAndRefundViewResponse responseList=client.execute(requestList);
        String s="";*//*


        PopAfsRefundapplyQuerylistRequest request2=new PopAfsRefundapplyQuerylistRequest();
//        request2.setStatus("1");
//        request.setId("111");
//        request.setOrderId("1234");
//        request.setBuyerId("abc");
//        request.setBuyerName("abc");
        request2.setApplyTimeStart(startTimeStr);
        request2.setApplyTimeEnd(endTimeStr);
//        request.setCheckTimeStart("2023-12-01 16:11:40");
//        request.setCheckTimeEnd("2023-12-31 16:11:40");
        request2.setPageIndex(1);
        request2.setPageSize(100);
        PopAfsRefundapplyQuerylistResponse response2=client.execute(request2);

        // 用于更新状态
        // https://open.jd.com/home/home/#/doc/api?apiCateId=241&apiId=2171&apiName=jingdong.asc.sync.list


        // https://open.jd.com/home/home/#/doc/api?apiCateId=241&apiId=2136&apiName=jingdong.asc.query.list
        AscQueryListRequest request = new AscQueryListRequest();
        request.setBuId(sellerId);
        request.setOperatePin("testPin");
        request.setOperateNick("testPin");
        request.setApplyTimeBegin(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
        request.setApplyTimeEnd(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        request.setPageNumber(1);
        request.setPageSize(100);
        AscQueryListResponse response = client.execute(request);
        int insertSuccess = 0;
        int totalError = 0;
        int hasExist = 0;
        if (response != null && response.getPageResult() != null) {
            if (response.getPageResult().getData() != null) {
                for (var item : response.getPageResult().getData()) {
                    JdOrderAfter after = new JdOrderAfter();
                    BeanUtils.copyProperties(item, after);
//                    // 详情
//                    https://open.jd.com/home/home/#/doc/api?apiCateId=241&apiId=2118&apiName=jingdong.asc.query.view
//                    AscQueryViewRequest request2=new AscQueryViewRequest();
//                    request2.setBuId("10706");
//                    request2.setOperatePin("testPin");
//                    request2.setOperateNick("testPin");
//                    request2.setServiceId(item.getServiceId());
//                    request2.setOrderId(item.getOrderId());
//                    AscQueryViewResponse response2=client.execute(request2);


                    after.setShopId(params.getShopId());
                    var result = afterService.saveAfter(params.getShopId(), after);
                    if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                        //已经存在
                        hasExist++;
                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE, item.getServiceId().toString()));
                    } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                        insertSuccess++;
                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE, item.getServiceId().toString()));
                    } else {
                        totalError++;
                    }
                }
            }
        }*/
        if (lasttime == null) {
            // 新增
            OShopPullLasttime insertLasttime = new OShopPullLasttime();
            insertLasttime.setShopId(shopId);
            insertLasttime.setCreateTime(new Date());
            insertLasttime.setLasttime(endTime);
            insertLasttime.setPullType("REFUND");
            pullLasttimeService.save(insertLasttime);

        } else {
            // 修改
            OShopPullLasttime updateLasttime = new OShopPullLasttime();
            updateLasttime.setId(lasttime.getId());
            updateLasttime.setUpdateTime(new Date());
            updateLasttime.setLasttime(endTime);
            pullLasttimeService.updateById(updateLasttime);
        }
        OShopPullLogs logs = new OShopPullLogs();
        logs.setShopId(shopId);
        logs.setShopType(EnumShopType.JD.getIndex());
        logs.setPullType("REFUND");
        logs.setPullWay(pullWay);
        logs.setPullParams("{ApplyTimeBegin:" + startTimeStr + ",ApplyTimeEnd:" + endTimeStr + ",PageIndex:1,PageSize:100}");
        logs.setPullResult("{total:" + insertSuccess + ",hasExist:" + hasExist + ",totalError:" + totalError + "}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - beginTime);
        pullLogsService.save(logs);

    }
}
