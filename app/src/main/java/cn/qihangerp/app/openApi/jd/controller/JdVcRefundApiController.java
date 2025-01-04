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
import cn.qihangerp.sdk.jd.PullRequest;
import cn.qihangerp.sdk.jd.VcRefundApiHelper;
import cn.qihangerp.module.open.jd.domain.JdVcRefund;
import cn.qihangerp.sdk.jd.response.JdVcRefundResponse;
import cn.qihangerp.module.open.jd.service.JdVcRefundService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequestMapping("/api/open-api/jdvc/refund")
@RestController
@AllArgsConstructor
public class JdVcRefundApiController {
    private final JdApiCommon jdApiCommon;
    private final OShopPullLogsService pullLogsService;
    private final MqUtils mqUtils;
    private final OShopPullLasttimeService pullLasttimeService;
    private final JdVcRefundService jdVcRefundService;
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
//        String sellerId = checkResult.getData().getSellerId();

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

        String startTimeStr = startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String endTimeStr = endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExist = 0;//已存在的订单数
        ApiResultVo<JdVcRefundResponse> resultVo = VcRefundApiHelper.pullAfterSale(startTime, endTime, serverUrl, appKey, appSecret, accessToken);
        if(resultVo.getCode()!=0)return AjaxResult.error(resultVo.getMsg());

        for (var refund : resultVo.getList()){
            JdVcRefund jdvcRefund = new JdVcRefund();
            BeanUtils.copyProperties(refund,jdvcRefund);
            var result = jdVcRefundService.saveRefund(params.getShopId(), jdvcRefund);
            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                //已经存在
                hasExist++;
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, jdvcRefund.getId().toString()));
            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                insertSuccess++;
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, jdvcRefund.getId().toString()));
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
        logs.setShopType(EnumShopType.JDVC.getIndex());
        logs.setPullType("REFUND");
        logs.setPullWay("主动拉取");
        logs.setPullParams("{ApplyTimeBegin:" + startTimeStr + ",ApplyTimeEnd:" + endTimeStr + ",PageIndex:1,PageSize:50}");
        logs.setPullResult("{total:" + insertSuccess + ",hasExist:" + hasExist + ",totalError:" + totalError + "}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - beginTime);
        pullLogsService.save(logs);
        String msg = "成功{startTime:"+startTimeStr+",endTime:"+endTimeStr+"}总共找到：" + resultVo.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExist + "条";
        return AjaxResult.success(msg);
    }

}
