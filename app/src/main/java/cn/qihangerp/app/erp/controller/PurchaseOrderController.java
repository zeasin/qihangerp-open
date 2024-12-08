package cn.qihangerp.app.erp.controller;


import cn.qihangerp.app.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.scm.domain.ScmPurchaseOrder;
import cn.qihangerp.module.scm.request.PurchaseOrderAddRequest;
import cn.qihangerp.module.scm.request.PurchaseOrderOptionRequest;
import cn.qihangerp.module.scm.request.SearchRequest;
import cn.qihangerp.module.scm.service.ScmPurchaseOrderService;
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
public class PurchaseOrderController extends BaseController
{
    private final ScmPurchaseOrderService scmPurchaseOrderService;
    /**
     *
     */
    @GetMapping("/list")
    public TableDataInfo list(SearchRequest bo, PageQuery pageQuery)
    {
        PageResult<ScmPurchaseOrder> pageResult = scmPurchaseOrderService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    @GetMapping(value = "/detail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ScmPurchaseOrder detail = scmPurchaseOrderService.getDetailById(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/create")
    public AjaxResult add(@RequestBody PurchaseOrderAddRequest addBo)
    {
        addBo.setCreateBy(getUsername());
        return toAjax(scmPurchaseOrderService.createPurchaseOrder(addBo));
    }
    @PutMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody PurchaseOrderOptionRequest request)
    {
        request.setUpdateBy(getUsername());
        int result = scmPurchaseOrderService.updateScmPurchaseOrder(request);
        if(result == -1){
            return new AjaxResult(0,"状态不正确");
        }else{
            return toAjax(result);
        }

    }
}
