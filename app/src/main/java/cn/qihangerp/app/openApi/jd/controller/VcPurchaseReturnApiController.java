package cn.qihangerp.app.openApi.jd.controller;//package cn.qihangerp.jd.controller.vc;
//
//import cn.qihangerp.common.AjaxResult;
//import cn.qihangerp.common.ResultVo;
//import cn.qihangerp.common.ResultVoEnum;
//import cn.qihangerp.common.enums.EnumShopType;
//import cn.qihangerp.common.enums.HttpStatus;
//import cn.qihangerp.common.mq.MqMessage;
//import cn.qihangerp.common.mq.MqType;
//import cn.qihangerp.common.mq.MqUtils;
//import cn.qihangerp.open.jd.domain.*;
//import cn.qihangerp.jd.openApi.ApiCommon;
//import cn.qihangerp.jd.openApi.PullRequest;
//import cn.qihangerp.open.jd.service.JdVcReturnService;
//import cn.qihangerp.open.jd.service.SysShopPullLasttimeService;
//import cn.qihangerp.open.jd.service.SysShopPullLogsService;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;
//
//@RequestMapping("/vc/return")
//@RestController
//@AllArgsConstructor
//public class VcPurchaseReturnApiController {
//    private final ApiCommon apiCommon;
//    private final SysShopPullLogsService pullLogsService;
//    private final MqUtils mqUtils;
//    private final SysShopPullLasttimeService pullLasttimeService;
//
//    private final JdVcReturnService jdVcReturnService;
//    /**
//     * 拉取售后数据
//     *
//     * @param params
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping(value = "/pull_list", method = RequestMethod.POST)
//    public AjaxResult pullList(@RequestBody PullRequest params) throws Exception {
//        if (params.getShopId() == null || params.getShopId() <= 0) {
//            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
//        }
//        Date currDateTime = new Date();
//        long beginTime = System.currentTimeMillis();
//        var checkResult = apiCommon.checkBefore(params.getShopId());
//        if (checkResult.getCode() != HttpStatus.SUCCESS) {
//            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg(), checkResult.getData());
//        }
//        String accessToken = checkResult.getData().getAccessToken();
//        String serverUrl = checkResult.getData().getServerUrl();
//        String appKey = checkResult.getData().getAppKey();
//        String appSecret = checkResult.getData().getAppSecret();
//        String sellerId = checkResult.getData().getSellerId();
//
//        // 获取最后更新时间
//        LocalDateTime startTime = null;
//        LocalDateTime endTime = null;
//        SysShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(params.getShopId(), "REFUND");
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
//        if(resultVo.getCode()!=0)return AjaxResult.error(resultVo.getMsg());
//
//        for (var jdVcReturn : resultVo.getList()){
//
//            var result = jdVcReturnService.saveReturn(params.getShopId(), jdVcReturn);
//            if (result.getCode() == ResultVoEnum.DataExist.getIndex()) {
//                //已经存在
//                hasExist++;
//                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, jdVcReturn.getReturnId().toString()));
//            } else if (result.getCode() == ResultVoEnum.SUCCESS.getIndex()) {
//                insertSuccess++;
//                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, jdVcReturn.getReturnId().toString()));
//            } else {
//                totalError++;
//            }
//        }
//
//        if (lasttime == null) {
//            // 新增
//            SysShopPullLasttime insertLasttime = new SysShopPullLasttime();
//            insertLasttime.setShopId(params.getShopId());
//            insertLasttime.setCreateTime(new Date());
//            insertLasttime.setLasttime(endTime);
//            insertLasttime.setPullType("REFUND");
//            pullLasttimeService.save(insertLasttime);
//
//        } else {
//            // 修改
//            SysShopPullLasttime updateLasttime = new SysShopPullLasttime();
//            updateLasttime.setId(lasttime.getId());
//            updateLasttime.setUpdateTime(new Date());
//            updateLasttime.setLasttime(endTime);
//            pullLasttimeService.updateById(updateLasttime);
//        }
//        SysShopPullLogs logs = new SysShopPullLogs();
//        logs.setShopId(params.getShopId());
//        logs.setShopType(EnumShopType.JD.getIndex());
//        logs.setPullType("REFUND");
//        logs.setPullWay("主动拉取");
//        logs.setPullParams("{ApplyTimeBegin:" + startTimeStr + ",ApplyTimeEnd:" + endTimeStr + ",PageIndex:1,PageSize:100}");
//        logs.setPullResult("{total:" + insertSuccess + ",hasExist:" + hasExist + ",totalError:" + totalError + "}");
//        logs.setPullTime(currDateTime);
//        logs.setDuration(System.currentTimeMillis() - beginTime);
//        pullLogsService.save(logs);
////        mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE,item.getId()));
//        return AjaxResult.success();
//    }
//
//}
