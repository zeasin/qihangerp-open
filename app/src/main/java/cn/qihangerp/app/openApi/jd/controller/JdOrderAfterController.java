package cn.qihangerp.app.openApi.jd.controller;

import cn.qihangerp.app.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.module.open.jd.domain.JdRefund;
import cn.qihangerp.module.open.jd.domain.bo.JdAfterBo;
import cn.qihangerp.module.open.jd.domain.bo.JdOrderPushBo;
import cn.qihangerp.module.open.jd.service.JdOrderService;
import cn.qihangerp.module.open.jd.service.JdRefundService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/open-api/jd/after")
public class JdOrderAfterController extends BaseController {
    private final JdOrderService orderService;
//    private final JdRefundService refundService;
//    private final JdOrderAfterService afterService;
    private final JdRefundService afterSaleService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(JdAfterBo bo, PageQuery pageQuery) {
        PageResult<JdRefund> result = afterSaleService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @PostMapping("/push_oms")
    @ResponseBody
    public AjaxResult pushOms(@RequestBody JdOrderPushBo bo) {
        // TODO:需要优化消息格式
        if(bo!=null && bo.getIds()!=null) {
            for(String id: bo.getIds()) {
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JD, MqType.REFUND_MESSAGE, id));
            }
        }
        return success();
    }
}
