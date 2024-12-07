package cn.qihangerp.app.openApi.jd.controller;//package cn.qihangerp.jd.controller.vc;
//
//import cn.qihangerp.common.AjaxResult;
//import cn.qihangerp.common.PageQuery;
//import cn.qihangerp.common.PageResult;
//import cn.qihangerp.common.TableDataInfo;
//import cn.qihangerp.common.enums.EnumShopType;
//import cn.qihangerp.common.mq.MqMessage;
//import cn.qihangerp.common.mq.MqType;
//import cn.qihangerp.common.mq.MqUtils;
//import cn.qihangerp.open.jd.domain.JdVcReturn;
//import cn.qihangerp.open.jd.domain.bo.JdOrderPushBo;
//import cn.qihangerp.open.jd.domain.bo.JdVcReturnBo;
//import cn.qihangerp.open.jd.service.JdVcReturnService;
//import cn.qihangerp.security.common.BaseController;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@AllArgsConstructor
//@RestController
//@RequestMapping("/vc/return")
//public class VcPurchaseReturnController extends BaseController {
//
//    private final JdVcReturnService returnService;
//    private final MqUtils mqUtils;
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    public TableDataInfo returnList(JdVcReturnBo bo, PageQuery pageQuery) {
//        PageResult<JdVcReturn> result = returnService.queryPageList(bo, pageQuery);
//
//        return getDataTable(result);
//    }
//
//    @PostMapping("/push_oms")
//    @ResponseBody
//    public AjaxResult pushOms(@RequestBody JdOrderPushBo bo) {
//        // TODO:需要优化消息格式
//        if(bo!=null && bo.getIds()!=null) {
//            for(String id: bo.getIds()) {
//                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, id));
//            }
//        }
//        return success();
//    }
//}
