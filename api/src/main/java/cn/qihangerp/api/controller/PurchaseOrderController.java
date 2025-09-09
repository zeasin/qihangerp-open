package cn.qihangerp.api.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.erp.bo.SearchBo;
import cn.qihangerp.module.erp.domain.ErpPurchaseOrder;
import cn.qihangerp.module.erp.service.ErpPurchaseOrderService;
import cn.qihangerp.security.common.BaseController;
import cn.qihangerp.security.common.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/erp/purchase")
public class PurchaseOrderController extends BaseController
{
    private final ErpPurchaseOrderService erpPurchaseOrderService;
    /**
     *
     */
    @GetMapping("/list")
    public TableDataInfo list(SearchBo bo, PageQuery pageQuery)
    {
        PageResult<ErpPurchaseOrder> pageResult = erpPurchaseOrderService.queryPageList(bo, pageQuery);
        return getDataTable(pageResult);
    }

    @GetMapping(value = "/detail/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        ErpPurchaseOrder detail = erpPurchaseOrderService.getDetailById(id);
        return AjaxResult.success(detail);
    }

    @PostMapping("/create")
    public AjaxResult add(@RequestBody PurchaseOrderAddRequest addBo, HttpServletRequest request)
    {
        Integer userIdentity = SecurityUtils.getUserIdentity();
        Long merchantId = 0l;
        if(userIdentity == null||userIdentity==0){
            merchantId = 0l;
        }else if(userIdentity==20){
            merchantId = SecurityUtils.getDeptId();
        }else{
            merchantId = -1L;
        }
        addBo.setMerchantId(merchantId);
        addBo.setCreateBy(getUsername());
        return toAjax(erpPurchaseOrderService.createPurchaseOrder(addBo));
    }
    @PutMapping("/updateStatus")
    public AjaxResult updateStatus(@RequestBody PurchaseOrderOptionRequest req, HttpServletRequest request)
    {
        req.setUpdateBy(getUsername());
        int result = erpPurchaseOrderService.updateScmPurchaseOrder(req);
        if(result == -1){
            return new AjaxResult(0,"状态不正确");
        }else{
            return toAjax(result);
        }

    }
}
