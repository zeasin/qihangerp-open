package cn.qihangerp.app.openApi.jd.controller;

import cn.qihangerp.app.openApi.jd.JdApiCommon;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.domain.OShopPullLasttime;
import cn.qihangerp.domain.OShopPullLogs;
import cn.qihangerp.module.service.OShopPullLasttimeService;
import cn.qihangerp.module.service.OShopPullLogsService;

import cn.qihangerp.sdk.common.ApiResultVo;
import cn.qihangerp.sdk.jd.AfterSaleApiHelper;
import cn.qihangerp.sdk.jd.PullRequest;
import cn.qihangerp.module.open.jd.domain.JdRefund;
import cn.qihangerp.sdk.jd.response.JdRefundResponse;
import cn.qihangerp.module.open.jd.service.JdRefundService;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.shangjiashouhou.AscSyncListRequest;
import com.jd.open.api.sdk.response.shangjiashouhou.AscSyncListResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequestMapping("/api/open-api/jd/refund")
@RestController
@AllArgsConstructor
public class JdRefundApiController {
    private final JdApiCommon jdApiCommon;
    private final OShopPullLogsService pullLogsService;
//    private final JdRefundService refundService;
    private final MqUtils mqUtils;
    private final OShopPullLasttimeService pullLasttimeService;
//    private final JdOrderAfterService afterService;
    private final JdRefundService afterSaleService;

    /**
     * 拉取售后数据
     *
     * @param params
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_list", method = RequestMethod.POST)
    public AjaxResult pullList(@RequestBody PullRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();
        var checkResult = jdApiCommon.checkBefore(params.getShopId());
        if (checkResult.getCode() != HttpStatus.SUCCESS) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(), checkResult.getData());
        }
        String accessToken = checkResult.getData().getAccessToken();
        String serverUrl = checkResult.getData().getServerUrl();
        String appKey = checkResult.getData().getAppKey();
        String appSecret = checkResult.getData().getAppSecret();
        Long sellerId = checkResult.getData().getSellerId();

        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(params.getShopId(), "REFUND");
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

//        endTime = LocalDateTime.now();
//        endTime = endTime.minusHours(12);
//        startTime = endTime.minusDays(1);

        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        //获取退款
        ApiResultVo<JdRefundResponse> refundVo = AfterSaleApiHelper.pullOrderCancel(startTime,endTime,serverUrl,appKey,appSecret,accessToken);
        if(refundVo.getCode()!=0) return AjaxResult.error(ResultVoEnum.SystemException,refundVo.getMsg());
        //获取售后和退款
        ApiResultVo<JdRefundResponse> jdAfterSaleResultVo = AfterSaleApiHelper.pullServiceAndRefund(sellerId, startTime, endTime, serverUrl, appKey, appSecret, accessToken);
        if(jdAfterSaleResultVo.getCode()!=0) return AjaxResult.error(ResultVoEnum.CONFIG_ERROR,jdAfterSaleResultVo.getMsg());
//        //获取售后服务list
//        ResultVo<JdRefund> afterSaleVo = AfterSaleApiHelper.pullServiceList(sellerId,startTime,endTime,serverUrl,appKey,appSecret,accessToken);
//        if(afterSaleVo.getCode()!=0) return AjaxResult.error(ResultVoEnum.CONFIG_ERROR,afterSaleVo.getMsg());
        // 合并两个列表
        List<JdRefundResponse> mergedList = Stream.of(refundVo.getList(), jdAfterSaleResultVo.getList())
                .flatMap(List::stream)
                .collect(Collectors.toList());

//        mergedList.sort(Comparator.comparing(JdAfterSale::getApplyTime));

        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExist = 0;//已存在的订单数
        //循环插入订单数据到数据库
        for (var after : mergedList) {
            JdRefund jdRefund = new JdRefund();
            BeanUtils.copyProperties(after,jdRefund);
            jdRefund.setShopId(params.getShopId());
            var result = afterSaleService.saveRefund(params.getShopId(), jdRefund);
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

        if(totalError==0) {
            if (lasttime == null) {
                // 新增
                OShopPullLasttime insertLasttime = new OShopPullLasttime();
                insertLasttime.setShopId(params.getShopId());
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
        }
        OShopPullLogs logs = new OShopPullLogs();
        logs.setShopId(params.getShopId());
        logs.setShopType(EnumShopType.JD.getIndex());
        logs.setPullType("REFUND");
        logs.setPullWay("主动拉取");
        logs.setPullParams("{ApplyTimeBegin:" + startTimeStr + ",ApplyTimeEnd:" + endTimeStr + ",PageIndex:1,PageSize:100}");
        logs.setPullResult("{total:" + insertSuccess + ",hasExist:" + hasExist + ",totalError:" + totalError + "}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - beginTime);
        pullLogsService.save(logs);
//        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE,item.getId()));
        return AjaxResult.success();
    }

    @RequestMapping(value = "/pull_update_status", method = RequestMethod.POST)
    public AjaxResult pullUpdateStatus(@RequestBody PullRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();
        var checkResult = jdApiCommon.checkBefore(params.getShopId());
        if (checkResult.getCode() != HttpStatus.SUCCESS) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(), checkResult.getData());
        }
        String accessToken = checkResult.getData().getAccessToken();
        String serverUrl = checkResult.getData().getServerUrl();
        String appKey = checkResult.getData().getAppKey();
        String appSecret = checkResult.getData().getAppSecret();
        Long sellerId = checkResult.getData().getSellerId();

        // 取24小时内的数据
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(1);


        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        JdClient client = new DefaultJdClient(serverUrl, accessToken, appKey, appSecret);

        // 用于更新状态
        // https://open.jd.com/home/home/#/doc/api?apiCateId=241&apiId=2171&apiName=jingdong.asc.sync.list
        AscSyncListRequest request1 = new AscSyncListRequest();
        request1.setBuId(sellerId.toString());
        request1.setOperatePin("testPin");
        request1.setOperateNick("testPin");
        request1.setUpdateTimeBegin(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
        request1.setUpdateTimeEnd(Date.from(endTime.atZone(ZoneId.systemDefault()).toInstant()));
        request1.setPageNumber(1);
        request1.setPageSize(100);
        AscSyncListResponse response = client.execute(request1);
        if (response != null && response.getPageResult() != null) {
            if (response.getPageResult().getData() != null) {
                for (var item : response.getPageResult().getData()) {
//                    JdOrderAfter after = new JdOrderAfter();
//                    BeanUtils.copyProperties(item, after);
//                    after.setShopId(params.getShopId());
//                    var result = afterService.updateAfterStatusByServiceId(after);
//                    if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
//                        // 更新成功，发送通知
//                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE,result.getData().toString()));
//                    }
                }
            }
        }

        OShopPullLogs logs = new OShopPullLogs();
        logs.setShopId(params.getShopId());
        logs.setShopType(EnumShopType.JD.getIndex());
        logs.setPullType("REFUND");
        logs.setPullWay("主动更新状态");
        logs.setPullParams("{ApplyTimeBegin:" + startTimeStr + ",ApplyTimeEnd:" + endTimeStr + ",PageIndex:1,PageSize:100}");
        logs.setPullResult("{total:,hasExist: ,totalError: }");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - beginTime);
        pullLogsService.save(logs);
//        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE,item.getId()));
        return AjaxResult.success();
    }
}
