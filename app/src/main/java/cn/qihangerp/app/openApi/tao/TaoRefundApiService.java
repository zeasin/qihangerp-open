package cn.qihangerp.app.openApi.tao;

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
import cn.qihangerp.sdk.tao.RefundApiHelper;
import cn.qihangerp.module.open.tao.domain.TaoRefund;
import cn.qihangerp.sdk.tao.response.TaoRefundResponse;
import cn.qihangerp.module.open.tao.service.TaoRefundService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Log
@AllArgsConstructor
@Service
public class TaoRefundApiService {
    private final OShopPullLasttimeService pullLasttimeService;
    private final OShopPullLogsService pullLogsService;
    private final TaoRefundService refundService;
    private final MqUtils mqUtils;
    public void pullRefund(String pullWay,Long shopId,String serverUrl,String appKey,String appSecret,String sessionKey) {
        log.info("/**************主动更新tao退货订单****************/");
        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();

        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime  endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(shopId, "REFUND");
        if(lasttime == null){
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(1);
        }else{
            startTime = lasttime.getLasttime().minusHours(1);//取上次结束一个小时前
            endTime = startTime.plusDays(1);//取24小时
            if(endTime.isAfter(LocalDateTime.now())){
                endTime = LocalDateTime.now();
            }
        }

        try {
            //第一次获取
            ApiResultVo<TaoRefundResponse> upResult = RefundApiHelper.pullRefund(startTime, endTime, serverUrl, appKey, appSecret, sessionKey);

            if (upResult.getCode() != 0) {
                log.info("/**************主动更新tao退货订单：第一次获取结果失败：" + upResult.getMsg() + "****************/");
//            return new ApiResult<>(EnumResultVo.SystemException.getIndex(), upResult.getMsg());
                return;
            }

            log.info("/**************主动更新tao退货订单：第一次获取结果：总记录数" + upResult.getTotalRecords() + "****************/");
            int insertSuccess = 0;//新增成功的订单
            int totalError = 0;
            int hasExistOrder = 0;//已存在的订单数

            //循环插入订单数据到数据库
            for (var refund : upResult.getList()) {
                TaoRefund taoRefund = new TaoRefund();
                BeanUtils.copyProperties(refund,taoRefund);
                //插入订单数据
                var result = refundService.saveAndUpdateRefund(shopId, taoRefund);
                if (result == ResultVoEnum.DataExist.getIndex()) {
                    //已经存在
                    mqUtils.sendApiMessage(MqMessage.build(EnumShopType.TAO, MqType.REFUND_MESSAGE, refund.getRefundId()));
                    log.info("/**************主动更新tao退货订单：开始更新数据库：" + refund.getRefundId() + "存在、更新****************/");
                    hasExistOrder++;
                } else if (result == ResultVoEnum.SUCCESS.getIndex()) {
                    log.info("/**************主动更新tao退货订单：开始插入数据库：" + refund.getRefundId() + "不存在、新增****************/");
                    mqUtils.sendApiMessage(MqMessage.build(EnumShopType.TAO, MqType.REFUND_MESSAGE, refund.getRefundId()));
                    insertSuccess++;
                } else {
                    log.info("/**************主动更新tao退货订单：开始更新数据库：" + refund.getRefundId() + "报错****************/");
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

            String msg = "成功，总共找到：" + upResult.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
            OShopPullLogs logs = new OShopPullLogs();
            logs.setShopType(EnumShopType.TAO.getIndex());
            logs.setShopId(shopId);
            logs.setPullType("REFUND");
            logs.setPullWay(pullWay);
            logs.setPullParams("{startTime:" + startTime + ",endTime:" + endTime + "}");
            logs.setPullResult("{insert:" + insertSuccess + ",update:" + hasExistOrder + ",fail:" + totalError + "}");
            logs.setPullTime(currDateTime);
            logs.setDuration(System.currentTimeMillis() - beginTime);
            pullLogsService.save(logs);
            log.info("/**************主动更新tao订单：END：" + msg + "****************/");

        }catch (Exception e){
            pullLogsService.save(new OShopPullLogs(shopId, EnumShopType.TAO.getIndex(), "REFUND", pullWay, "", e.getMessage(), currDateTime, System.currentTimeMillis() - beginTime));
        }
    }
}
