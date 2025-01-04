package cn.qihangerp.app.openApi.tao;

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
import cn.qihangerp.sdk.common.ApiResultVoEnum;
import cn.qihangerp.sdk.tao.OrderApiHelper;
import cn.qihangerp.module.open.tao.domain.TaoOrder;
import cn.qihangerp.sdk.tao.response.TaoOrderResponse;
import cn.qihangerp.module.open.tao.service.TaoOrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@AllArgsConstructor
@Service
public class TaoOrderApiService {
    private final OShopPullLasttimeService pullLasttimeService;
    private final OShopPullLogsService pullLogsService;
    private final TaoOrderService orderService;
    private final MqUtils mqUtils;
    public void pullOrder(String pullWay,Long shopId,String serverUrl,String appKey,String appSecret,String sessionKey) {
        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();
        Long pageSize = 100l;
        Long pageIndex = 1l;
        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(shopId, "ORDER");
        if (lasttime == null) {
            endTime = LocalDateTime.now();
            startTime = endTime.minusDays(1);
        } else {
            startTime = lasttime.getLasttime().minusHours(1);//取上次结束一个小时前
            endTime = startTime.plusDays(1);//取24小时
            if (endTime.isAfter(LocalDateTime.now())) {
                endTime = LocalDateTime.now();
            }
        }

        //获取
        try {
            ApiResultVo<TaoOrderResponse> upResult = OrderApiHelper.pullIncrementOrder(startTime, endTime, pageIndex, pageSize, serverUrl, appKey, appSecret, sessionKey);
            if (upResult.getCode() == ApiResultVoEnum.SUCCESS.getIndex()) {
                int insertSuccess = 0;//新增成功的订单
                int totalError = 0;
                int hasExistOrder = 0;//已存在的订单数
                //循环插入订单数据到数据库
                for (var order : upResult.getList()) {
                    TaoOrder taoOrder = new TaoOrder();
                    BeanUtils.copyProperties(order,taoOrder);
                    //插入订单数据
                    var result = orderService.saveOrder(shopId, taoOrder);
                    if (result.getCode() == ApiResultVoEnum.DataExist.getIndex()) {
                        //已经存在
                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.TAO, MqType.ORDER_MESSAGE, order.getTid().toString()));
                        hasExistOrder++;
                    } else if (result.getCode() == ApiResultVoEnum.SUCCESS.getIndex()) {
                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.TAO, MqType.ORDER_MESSAGE, order.getTid().toString()));
                        insertSuccess++;
                    } else {
                        totalError++;
                    }
                }

                if (lasttime == null) {
                    // 新增
                    pullLasttimeService.save(new OShopPullLasttime(shopId,"ORDER",endTime,new Date(),null));

                } else {
                    // 修改
                    OShopPullLasttime updateLasttime = new OShopPullLasttime();
                    updateLasttime.setId(lasttime.getId());
                    updateLasttime.setUpdateTime(new Date());
                    updateLasttime.setLasttime(endTime);
                    pullLasttimeService.updateById(updateLasttime);
                }

                OShopPullLogs logs = new OShopPullLogs();
                logs.setShopType(EnumShopType.TAO.getIndex());
                logs.setShopId(shopId);
                logs.setPullType("ORDER");
                logs.setPullWay(pullWay);
                logs.setPullParams("{startTime:" + startTime + ",endTime:" + endTime + "}");
                logs.setPullResult("{insert:" + insertSuccess + ",update:" + hasExistOrder + ",fail:" + totalError + "}");
                logs.setPullTime(currDateTime);
                logs.setDuration(System.currentTimeMillis() - beginTime);
                pullLogsService.save(logs);

//                String msg = "成功，总共找到：" + upResult.getTotalRecords() + "条订单，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";

            } else {
                String errorMsg = "";
                if (upResult.getCode() == HttpStatus.UNAUTHORIZED) {
                    errorMsg = "Token已过期，请重新授权";
                } else {
                    errorMsg = upResult.getMsg();
                }
                pullLogsService.save(new OShopPullLogs(shopId, EnumShopType.TAO.getIndex(), "ORDER", pullWay, "", errorMsg, currDateTime, System.currentTimeMillis() - beginTime));
            }
        }catch (Exception e){
            pullLogsService.save(new OShopPullLogs(shopId, EnumShopType.TAO.getIndex(), "ORDER", pullWay, "", e.getMessage(), currDateTime, System.currentTimeMillis() - beginTime));
        }
    }
}
