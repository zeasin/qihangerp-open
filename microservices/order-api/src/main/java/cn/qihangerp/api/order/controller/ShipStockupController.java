package cn.qihangerp.api.order.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.model.bo.ShipStockUpBo;
import cn.qihangerp.model.bo.ShipStockUpCompleteBo;
import cn.qihangerp.module.order.service.OOrderShipListItemService;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/ship")
public class ShipStockupController  extends BaseController {
    private final OOrderShipListItemService orderShipListItemService;

    /**
     * 备货列表
     * @param bo
     * @param pageQuery
     * @return
     */
    @GetMapping("/stock_up_list")
    public TableDataInfo stock_up_list(ShipStockUpBo bo, PageQuery pageQuery)
    {
        var pageList = orderShipListItemService.queryPageList(bo,pageQuery);
        return getDataTable(pageList);
    }

    @PostMapping("/stock_up_complete")
    public AjaxResult stock_up_complete(@RequestBody ShipStockUpCompleteBo bo)
    {
        int result = orderShipListItemService.stockUpComplete(bo);
        if(result == -1) return AjaxResult.error("参数错误：orderItemIds为空");
        if(result == -2) return AjaxResult.error("参数错误：没有要添加的");
        else if(result == -1001) return AjaxResult.error("存在错误的orderItemId：状态不对不能生成出库单");
        else if(result == -1002) return AjaxResult.error("存在错误的订单数据：名单明细中没有skuId请修改！");
        //wmsStockOutEntryService.insertWmsStockOutEntry(wmsStockOutEntry)
        return toAjax(1);
    }

    @PostMapping("/stock_up_complete_by_order")
    public AjaxResult stock_up_completeByOrder(@RequestBody ShipStockUpCompleteBo bo)
    {
        int result = orderShipListItemService.stockUpCompleteByOrder(bo);
        if(result == -1) return AjaxResult.error("参数错误：orderItemIds为空");
        if(result == -2) return AjaxResult.error("参数错误：没有要添加的");
        else if(result == -1001) return AjaxResult.error("存在错误的orderItemId：状态不对不能生成出库单");
        else if(result == -1002) return AjaxResult.error("存在错误的订单数据：名单明细中没有skuId请修改！");
        //wmsStockOutEntryService.insertWmsStockOutEntry(wmsStockOutEntry)
        return toAjax(1);
    }
}
