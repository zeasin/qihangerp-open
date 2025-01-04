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
import cn.qihangerp.sdk.jd.VcRefundApiHelper;
import cn.qihangerp.module.open.jd.domain.JdVcRefund;
import cn.qihangerp.sdk.jd.response.JdVcRefundResponse;
import cn.qihangerp.module.open.jd.service.JdVcRefundService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@RequestMapping("/vc/refund")
@RestController
@AllArgsConstructor
public class VcRefundApiService {
    private final JdApiCommon apiCommon;
    private final OShopPullLogsService pullLogsService;
    private final MqUtils mqUtils;
    private final OShopPullLasttimeService pullLasttimeService;
    private final JdVcRefundService jdVcRefundService;
    /**
     * 拉取售后数据
     *
     * @param
     * @return
     * @throws
     */

    public void pullRefundList(String pullWay,Long shopId,String serverUrl,String appKey,String appSecret,String accessToken)  {
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

        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExist = 0;//已存在的订单数
        try {
            ApiResultVo<JdVcRefundResponse> resultVo = VcRefundApiHelper.pullAfterSale(startTime, endTime, serverUrl, appKey, appSecret, accessToken);
            if (resultVo.getCode() != 0) return;

            for (var refund : resultVo.getList()) {
                JdVcRefund jdVcRefund = new JdVcRefund();
                BeanUtils.copyProperties(refund,jdVcRefund);
                var result = jdVcRefundService.saveRefund(shopId, jdVcRefund);
                if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                    //已经存在
                    hasExist++;
                    mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, jdVcRefund.getId().toString()));
                } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                    insertSuccess++;
                    mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, jdVcRefund.getId().toString()));
                } else {
                    totalError++;
                }
            }

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
            logs.setShopType(EnumShopType.JDVC.getIndex());
            logs.setPullType("REFUND");
            logs.setPullWay(pullWay);
            logs.setPullParams("{ApplyTimeBegin:" + startTimeStr + ",ApplyTimeEnd:" + endTimeStr + ",PageIndex:1,PageSize:50}");
            logs.setPullResult("{total:" + insertSuccess + ",hasExist:" + hasExist + ",totalError:" + totalError + "}");
            logs.setPullTime(currDateTime);
            logs.setDuration(System.currentTimeMillis() - beginTime);
            pullLogsService.save(logs);
//        String msg = "成功{startTime:"+startTimeStr+",endTime:"+endTimeStr+"}总共找到：" + resultVo.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExist + "条";
        }catch (Exception e){
            e.printStackTrace();
            pullLogsService.save(new OShopPullLogs(shopId, EnumShopType.JDVC.getIndex(), "REFUND", pullWay, "", e.getMessage(), currDateTime, System.currentTimeMillis() - beginTime));
        }
    }

}
