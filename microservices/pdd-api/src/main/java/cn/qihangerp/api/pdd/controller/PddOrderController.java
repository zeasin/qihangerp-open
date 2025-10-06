package cn.qihangerp.api.pdd.controller;


import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.module.open.pdd.domain.PddOrder;
import cn.qihangerp.module.open.pdd.domain.bo.PddOrderBo;
import cn.qihangerp.module.open.pdd.domain.bo.PddOrderConfirmBo;
import cn.qihangerp.module.open.pdd.domain.bo.PddOrderPushBo;
import cn.qihangerp.module.open.pdd.service.PddOrderService;
import cn.qihangerp.security.common.BaseController;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/pdd/order")
public class PddOrderController extends BaseController {
    private final PddOrderService orderService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(PddOrderBo bo, PageQuery pageQuery) {
        PageResult<PddOrder> result = orderService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }



    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(orderService.queryDetailById(id));
    }

    /**
     * 手动推送到系统
     * @param bo
     * @return
     */
    @PostMapping("/push_oms")
    @ResponseBody
    public AjaxResult pushOms(@RequestBody PddOrderPushBo bo) {
        // TODO:需要优化消息格式
        if(bo!=null && bo.getIds()!=null) {
            for(String id: bo.getIds()) {
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.PDD, MqType.ORDER_MESSAGE, id));
            }
        }
        return success();
    }

    @PostMapping("/confirmOrder")
    public AjaxResult confirmOrder(@RequestBody PddOrderConfirmBo bo) {
        log.info("=========确认订单======={}", JSONObject.toJSONString(bo));
        if(bo.getOrderId()==null) return AjaxResult.error("订单id不能为空");
        if(StringUtils.isEmpty(bo.getReceiver())) return AjaxResult.error("缺少参数：receiver");
        if(StringUtils.isEmpty(bo.getMobile())) return AjaxResult.error("缺少参数：mobile");
        if(StringUtils.isEmpty(bo.getProvince())) return AjaxResult.error("缺少参数：province");
        if(StringUtils.isEmpty(bo.getCity())) return AjaxResult.error("缺少参数：city");
        if(StringUtils.isEmpty(bo.getTown())) return AjaxResult.error("缺少参数：town");
        if(StringUtils.isEmpty(bo.getAddress())) return AjaxResult.error("缺少参数：address");
        var result = orderService.confirmOrder(bo);
        if(result.getCode()==0) return success();
        else return AjaxResult.error(result.getMsg());

    }
}
