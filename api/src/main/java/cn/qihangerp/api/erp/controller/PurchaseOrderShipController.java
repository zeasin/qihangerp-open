package cn.qihangerp.api.erp.controller;


import cn.qihangerp.api.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.scm.domain.ScmPurchaseOrderShip;
import cn.qihangerp.module.scm.request.PurchaseOrderStockInBo;
import cn.qihangerp.module.scm.request.SearchRequest;
import cn.qihangerp.module.scm.service.ScmPurchaseOrderShipService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 商品管理Controller
 * 
 * @author qihang
 * @date 2023-12-29
 */
@AllArgsConstructor
@RestController
@RequestMapping("/erp-api/scm/purchase")
public class PurchaseOrderShipController extends BaseController
{
    private final ScmPurchaseOrderShipService shipService;
    /**
     *
     */
    @GetMapping("/shipList")
    public TableDataInfo shipList(SearchRequest bo, PageQuery pageQuery)
    {
        return getDataTable(shipService.queryPageList(bo, pageQuery));
    }
    @GetMapping(value = "/shipDetail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ScmPurchaseOrderShip detail = shipService.getById(id);
        return AjaxResult.success(detail);
    }
    @PutMapping("/ship/confirmReceipt")
    public AjaxResult confirmReceipt(@RequestBody ScmPurchaseOrderShip scmPurchaseOrderShip)
    {
        scmPurchaseOrderShip.setUpdateBy(getUsername());
        return toAjax(shipService.updateScmPurchaseOrderShip(scmPurchaseOrderShip));
    }

    //createStockInEntry
    @PostMapping("/ship/createStockInEntry")
    public AjaxResult createStockInEntry(@RequestBody PurchaseOrderStockInBo bo)
    {
        if(bo.getId() == null) return AjaxResult.error("缺少参数id");

        bo.setCreateBy(getUsername());
        int result = shipService.createStockInEntry(bo);
        if(result == -1) return new AjaxResult(404,"采购物流不存在");
        else if (result == -2) return new AjaxResult(501,"未确认收货不允许操作");
        else if (result == -3) {
            return new AjaxResult(502,"已处理过了请勿重复操作");
        } else if (result == -4) {
            return new AjaxResult(503,"状态不正确不能操作");
        } else if (result == 1) {
            return toAjax(1);
        }else return toAjax(result);
    }

}
