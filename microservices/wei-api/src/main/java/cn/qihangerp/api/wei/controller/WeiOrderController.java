package cn.qihangerp.api.wei.controller;


import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.module.open.wei.domain.WeiOrder;
import cn.qihangerp.module.open.wei.domain.bo.WeiOrderConfirmBo;
import cn.qihangerp.module.open.wei.domain.bo.WeiOrderPushBo;
import cn.qihangerp.module.open.wei.service.WeiOrderService;
import cn.qihangerp.security.common.BaseController;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/wei/order")
public class WeiOrderController extends BaseController {
    private final WeiOrderService orderService;
    private final MqUtils mqUtils;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo orderList(WeiOrder bo, PageQuery pageQuery) {
        PageResult<WeiOrder> result = orderService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(orderService.queryDetailById(id));
    }

    @PostMapping("/push_oms")
    @ResponseBody
    public AjaxResult pushOms(@RequestBody WeiOrderPushBo bo) {
        // TODO:需要优化消息格式
        if(bo!=null && bo.getIds()!=null) {
            for(String id: bo.getIds()) {
                mqUtils.sendApiMessage(MqMessage.build(EnumShopType.WEI, MqType.ORDER_MESSAGE, id));
            }
        }
        return success();
    }
    @PostMapping("/confirmOrder")
    public AjaxResult confirmOrder(@RequestBody WeiOrderConfirmBo bo) {
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
