package cn.qihangerp.api.order.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.order.domain.OShipment;
import cn.qihangerp.module.order.service.OOrderService;
import cn.qihangerp.module.order.service.OShipmentService;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/shipping")
public class ShipmentController extends BaseController {
    private final OShipmentService shippingService;

    private final OOrderService orderService;
    @GetMapping("/list")
    public TableDataInfo list(OShipment shipping, PageQuery pageQuery)
    {
        return getDataTable(shippingService.queryPageList(shipping,pageQuery));
    }


    @GetMapping("/searchOrderConsignee")
    public TableDataInfo searchOrderConsignee(String consignee)
    {
        return getDataTable(orderService.searchOrderConsignee(consignee));
    }

    @GetMapping("/searchOrderItemByReceiverMobile")
    public TableDataInfo searchOrderItemByReceiverMobile(String receiverMobile)
    {
        return getDataTable(orderService.searchOrderItemByReceiverMobile(receiverMobile));
    }


}
