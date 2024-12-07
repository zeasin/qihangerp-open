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
import cn.qihangerp.open.jd.domain.JdVcRefund;
import cn.qihangerp.open.jd.domain.bo.JdOrderPushBo;
import cn.qihangerp.open.jd.domain.bo.JdVcRefundBo;
import cn.qihangerp.open.jd.service.JdVcRefundService;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/open-api/jdvc/refund")
public class JdVcRefundController extends BaseController {

    private final JdVcRefundService refundService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo refundList(JdVcRefundBo bo, PageQuery pageQuery) {
        PageResult<JdVcRefund> result = refundService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @PostMapping("/push_oms")
    @ResponseBody
    public AjaxResult pushOms(@RequestBody JdOrderPushBo bo) {
        // TODO:需要优化消息格式
        if(bo!=null && bo.getIds()!=null) {
            for(String id: bo.getIds()) {
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.JDVC, MqType.REFUND_MESSAGE, id));
            }
        }
        return success();
    }
}
