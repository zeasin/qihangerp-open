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
import cn.qihangerp.sdk.pdd.RefundApiHelper;
import cn.qihangerp.module.open.pdd.domain.PddRefund;
import cn.qihangerp.sdk.pdd.response.PddRefundResponse;
import cn.qihangerp.module.open.pdd.service.PddRefundService;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Log
@AllArgsConstructor
@Service
public class PddRefundApiService {
    private final PddRefundService refundService;
    private final MqUtils mqUtils;
    private final OShopPullLogsService pullLogsService;
    private final OShopPullLasttimeService pullLasttimeService;
    public void pullRefund(String pullWay,Long shopId,String appKey,String appSecret,String accessToken) throws Exception {
        log.info("/**************增量拉取pdd退款****************/");

        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();

        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime  endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(shopId, "REFUND");
        if(lasttime == null){
            endTime = LocalDateTime.now();
//            startTime = endTime.minusDays(1);
            startTime = endTime.minusMinutes(30);
        }else {
            startTime = lasttime.getLasttime().minusMinutes(5);//取上次结束5分钟前
            endTime = startTime.plusMinutes(30);//结束时间取开始时间之后30分钟
            if(endTime.isAfter(LocalDateTime.now())){
                endTime = LocalDateTime.now();
            }
        }
        String pullParams = "{startTime:"+startTime+",endTime:"+endTime+"}";
        //获取
        ApiResultVo<PddRefundResponse> upResult = RefundApiHelper.pullRefundList(appKey, appSecret, accessToken,startTime, endTime);


        if(upResult.getCode() !=0 ){
            OShopPullLogs logs = new OShopPullLogs();
            logs.setShopId(shopId);
            logs.setShopType(EnumShopType.DOU.getIndex());
            logs.setPullType("REFUND");
            logs.setPullWay(pullWay);
            logs.setPullParams(pullParams);
            logs.setPullResult(upResult.getMsg());
            logs.setPullTime(currDateTime);
            logs.setDuration(System.currentTimeMillis() - beginTime);
            pullLogsService.save(logs);
            return ;
        }

        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExistOrder = 0;//已存在的订单数

        //循环插入订单数据到数据库
        for (var refund : upResult.getList()) {
            PddRefund pddRefund = new PddRefund();
            BeanUtils.copyProperties(refund,pddRefund);
            //插入订单数据
            var result = refundService.saveRefund(shopId, pddRefund);
            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                //已经存在
                log.info("/**************主动更新pdd退款：开始更新数据库：" + refund.getId() + "存在、更新************开始通知****/");
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.PDD, MqType.REFUND_MESSAGE,refund.getId().toString()));
                hasExistOrder++;
            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                log.info("/**************主动更新pdd退款：开始更新数据库：" + refund.getId() + "不存在、新增************开始通知****/");
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.PDD,MqType.REFUND_MESSAGE,refund.getId().toString()));
                insertSuccess++;
            } else {
                log.info("/**************主动更新pdd退款：开始更新数据库：" + refund.getId() + "报错****************/");
                totalError++;
            }
        }

        if(lasttime == null){
            // 新增
            OShopPullLasttime insertLasttime = new OShopPullLasttime();
            insertLasttime.setShopId(shopId);
            insertLasttime.setCreateTime(new Date());
            insertLasttime.setLasttime(endTime);
            insertLasttime.setPullType("REFUND");
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
        logs.setPullType("REFUND");
        logs.setPullWay(pullWay);
        logs.setPullParams(pullParams);
        logs.setPullResult("{insert:"+insertSuccess+",update:"+hasExistOrder+",fail:"+totalError+"}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - beginTime);
        pullLogsService.save(logs);

        String msg = "成功{startTime:"+startTime.format(df)+",endTime:"+endTime.format(df)+"}总共找到：" + upResult.getTotalRecords() + "条，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
        log.info("/**************主动更新PDD退款：END：" + msg + "****************/");
    }

}
