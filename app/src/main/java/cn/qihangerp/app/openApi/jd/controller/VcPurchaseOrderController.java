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
//import cn.qihangerp.open.jd.domain.JdVcPurchaseOrder;
//import cn.qihangerp.open.jd.domain.bo.JdOrderBo;
//import cn.qihangerp.open.jd.domain.bo.JdOrderPushBo;
//import cn.qihangerp.open.jd.service.JdVcPurchaseOrderService;
//import cn.qihangerp.security.common.BaseController;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//@AllArgsConstructor
//@RestController
//@RequestMapping("/vc/order")
//public class VcPurchaseOrderController extends BaseController {
//    private final JdVcPurchaseOrderService jdVcPurchaseOrderService;
//    private final MqUtils mqUtils;
//    @RequestMapping(value = "/list", method = RequestMethod.GET)
//    public TableDataInfo orderList(JdOrderBo bo, PageQuery pageQuery) {
//        PageResult<JdVcPurchaseOrder> result = jdVcPurchaseOrderService.queryPageList(bo, pageQuery);
//
//        return getDataTable(result);
//    }
//    @PostMapping("/push_oms")
//    @ResponseBody
//    public AjaxResult pushOms(@RequestBody JdOrderPushBo bo) {
//        // TODO:需要优化消息格式
//        if(bo!=null && bo.getIds()!=null) {
//            for(String id: bo.getIds()) {
//                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.ORDER_MESSAGE, id));
//            }
//        }
//        return success();
//    }
//}
