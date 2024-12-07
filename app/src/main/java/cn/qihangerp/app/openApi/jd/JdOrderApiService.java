package cn.qihangerp.app.openApi.jd;

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
import cn.qihangerp.sdk.jd.OrderApiHelper;
import cn.qihangerp.sdk.jd.VcOrderApiHelper;
import cn.qihangerp.open.jd.domain.JdOrder;
import cn.qihangerp.open.jd.domain.JdVcOrder;
import cn.qihangerp.sdk.jd.response.JdOrderResponse;
import cn.qihangerp.sdk.jd.response.JdVcOrderResponse;
import cn.qihangerp.open.jd.service.JdOrderService;
import cn.qihangerp.open.jd.service.JdVcOrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@AllArgsConstructor
@Service
public class JdOrderApiService {
    private final OShopPullLasttimeService pullLasttimeService;
    private final OShopPullLogsService pullLogsService;
    private final JdOrderService orderService;
//    private final JdVcPurchaseOrderService jdVcPurchaseOrderService;
    private final JdVcOrderService jdVcOrderService;
    private final MqUtils mqUtils;

    /**
     * 拉取京东POP订单
     * @param pullWay
     * @param shopId
     * @param serverUrl
     * @param appKey
     * @param appSecret
     * @param sessionKey
     */
    public void pullOrder(String pullWay,Long shopId,String serverUrl,String appKey,String appSecret,String sessionKey) {
        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();
//        Long pageSize = 100l;
//        Long pageIndex = 1l;
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
            ApiResultVo<JdOrderResponse> upResult = OrderApiHelper.pullOrder(startTime, endTime, serverUrl, appKey, appSecret, sessionKey);
            if (upResult.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                int insertSuccess = 0;//新增成功的订单
                int totalError = 0;
                int hasExistOrder = 0;//已存在的订单数
                //循环插入订单数据到数据库
                for (var order : upResult.getList()) {
                    JdOrder jdOrder = new JdOrder();
                    BeanUtils.copyProperties(order,jdOrder);
                    //插入订单数据
                    var result = orderService.saveOrder(shopId, jdOrder);
                    if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                        //已经存在
                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.ORDER_MESSAGE, order.getOrderId()));
                        hasExistOrder++;
                    } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.ORDER_MESSAGE, order.getOrderId()));
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
                logs.setShopType(EnumShopType.JD.getIndex());
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
                pullLogsService.save(new OShopPullLogs(shopId, EnumShopType.JD.getIndex(), "ORDER", pullWay, "", errorMsg, currDateTime, System.currentTimeMillis() - beginTime));
            }
        }catch (Exception e){
            pullLogsService.save(new OShopPullLogs(shopId, EnumShopType.JD.getIndex(), "ORDER", pullWay, "", e.getMessage(), currDateTime, System.currentTimeMillis() - beginTime));
        }
    }

    /**
     * 拉取京东VC订单
     * @param pullWay
     * @param shopId
     * @param serverUrl
     * @param appKey
     * @param appSecret
     * @param sessionKey
     */
    public void pullVcOrder(String pullWay,Long shopId,String serverUrl,String appKey,String appSecret,String sessionKey) {
        Date currDateTime = new Date();
        long beginTime = System.currentTimeMillis();
//        Long pageSize = 100l;
//        Long pageIndex = 1l;
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
            //拉取订单
            ApiResultVo<JdVcOrderResponse> upResult = VcOrderApiHelper.pullOrder(startTime,endTime,serverUrl,appKey,appSecret,sessionKey);
            if (upResult.getCode() == ResultVoEnum.SUCCESS.getIndex()) {

                int insertSuccess = 0;//新增成功的订单
                int totalError = 0;
                int hasExistOrder = 0;//已存在的订单数
                //循环插入订单数据到数据库
                for (var order : upResult.getList()) {
                    JdVcOrder jdVcOrder = new JdVcOrder();
                    BeanUtils.copyProperties(order,jdVcOrder);
                    //插入订单数据
                    var result = jdVcOrderService.saveOrder(shopId.longValue(), jdVcOrder);
                    if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
                        //已经存在
                        hasExistOrder++;
                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC,MqType.ORDER_MESSAGE,order.getCustomOrderId().toString()));
                    } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
                        insertSuccess++;
                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC,MqType.ORDER_MESSAGE,order.getCustomOrderId().toString()));
                    } else {
                        totalError++;
                    }
                }
                if(lasttime == null){
                    // 新增
                    OShopPullLasttime insertLasttime = new OShopPullLasttime();
                    insertLasttime.setShopId(shopId);
                    insertLasttime.setCreateTime(new Date());
                    insertLasttime.setLasttime(endTime);
                    insertLasttime.setPullType("ORDER");
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
                String paramsStr = "{startTime:"+startTime.format(df)+",endTime:"+endTime.format(df)+"}";
                String resultStr ="{insertSuccess:"+insertSuccess+",hasExistOrder:"+hasExistOrder+",totalError:"+totalError+"}";

                OShopPullLogs logs = new OShopPullLogs();
                logs.setShopType(EnumShopType.JDVC.getIndex());
                logs.setShopId(shopId);
                logs.setPullType("ORDER");
                logs.setPullWay("主动拉取");
                logs.setPullParams(paramsStr);
                logs.setPullResult(resultStr);
                logs.setPullTime(currDateTime);
                logs.setDuration(System.currentTimeMillis() - beginTime);
                pullLogsService.save(logs);
            } else {
                String errorMsg = "";
                if (upResult.getCode() == HttpStatus.UNAUTHORIZED) {
                    errorMsg = "Token已过期，请重新授权";
                } else {
                    errorMsg = upResult.getMsg();
                }
                pullLogsService.save(new OShopPullLogs(shopId, EnumShopType.JDVC.getIndex(), "ORDER", pullWay, "", errorMsg, currDateTime, System.currentTimeMillis() - beginTime));
            }
        }catch (Exception e){
            e.printStackTrace();
            pullLogsService.save(new OShopPullLogs(shopId, EnumShopType.JDVC.getIndex(), "ORDER", pullWay, "", e.getMessage(), currDateTime, System.currentTimeMillis() - beginTime));
        }
    }

//    public void pullVcPurchaseOrder(String pullWay,Integer shopId,String serverUrl,String appKey,String appSecret,String sessionKey) {
//        Date currDateTime = new Date();
//        long beginTime = System.currentTimeMillis();
////        Long pageSize = 100l;
////        Long pageIndex = 1l;
//        // 获取最后更新时间
//        LocalDateTime startTime = null;
//        LocalDateTime endTime = null;
//        SysShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(shopId, "ORDER");
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
//        //获取
//        try {
//            //拉取订单
//            ResultVo<JdVcPurchaseOrder> upResult = VcPurchaseOrderApiHelper.pullOrder(startTime,endTime,serverUrl,appKey,appSecret,sessionKey);
//            if (upResult.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
//
//                int insertSuccess = 0;//新增成功的订单
//                int totalError = 0;
//                int hasExistOrder = 0;//已存在的订单数
//                //循环插入订单数据到数据库
//                for (var order : upResult.getList()) {
//                    //插入订单数据
//                    var result = jdVcPurchaseOrderService.saveOrder(shopId.longValue(), order);
//                    if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
//                        //已经存在
//                        hasExistOrder++;
//                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC,MqType.ORDER_MESSAGE,order.getOrderId().toString()));
//                    } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
//                        insertSuccess++;
//                        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC,MqType.ORDER_MESSAGE,order.getOrderId().toString()));
//                    } else {
//                        totalError++;
//                    }
//                }
//                if(lasttime == null){
//                    // 新增
//                    SysShopPullLasttime insertLasttime = new SysShopPullLasttime();
//                    insertLasttime.setShopId(shopId);
//                    insertLasttime.setCreateTime(new Date());
//                    insertLasttime.setLasttime(endTime);
//                    insertLasttime.setPullType("ORDER");
//                    pullLasttimeService.save(insertLasttime);
//
//                }else {
//                    // 修改
//                    SysShopPullLasttime updateLasttime = new SysShopPullLasttime();
//                    updateLasttime.setId(lasttime.getId());
//                    updateLasttime.setUpdateTime(new Date());
//                    updateLasttime.setLasttime(endTime);
//                    pullLasttimeService.updateById(updateLasttime);
//                }
//                DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//                String paramsStr = "{startTime:"+startTime.format(df)+",endTime:"+endTime.format(df)+"}";
//                String resultStr ="{insertSuccess:"+insertSuccess+",hasExistOrder:"+hasExistOrder+",totalError:"+totalError+"}";
//
//                SysShopPullLogs logs = new SysShopPullLogs();
//                logs.setShopType(EnumShopType.JD.getIndex());
//                logs.setShopId(shopId);
//                logs.setPullType("ORDER");
//                logs.setPullWay("主动拉取");
//                logs.setPullParams(paramsStr);
//                logs.setPullResult(resultStr);
//                logs.setPullTime(currDateTime);
//                logs.setDuration(System.currentTimeMillis() - beginTime);
//                pullLogsService.save(logs);
//            } else {
//                String errorMsg = "";
//                if (upResult.getCode() == HttpStatus.UNAUTHORIZED) {
//                    errorMsg = "Token已过期，请重新授权";
//                } else {
//                    errorMsg = upResult.getMsg();
//                }
//                pullLogsService.save(new SysShopPullLogs(shopId, EnumShopType.JD.getIndex(), "ORDER", pullWay, "", errorMsg, currDateTime, System.currentTimeMillis() - beginTime));
//            }
//        }catch (Exception e){
//            pullLogsService.save(new SysShopPullLogs(shopId, EnumShopType.JD.getIndex(), "ORDER", pullWay, "", e.getMessage(), currDateTime, System.currentTimeMillis() - beginTime));
//        }
//    }
}
