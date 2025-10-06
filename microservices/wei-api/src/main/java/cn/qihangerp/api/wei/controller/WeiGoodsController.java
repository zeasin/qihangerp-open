package cn.qihangerp.api.wei.controller;


import cn.qihangerp.common.*;
import cn.qihangerp.model.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.module.goods.service.OGoodsSkuService;
import cn.qihangerp.module.open.wei.domain.WeiGoodsSku;
import cn.qihangerp.module.open.wei.service.OmsWeiGoodsSkuService;
import cn.qihangerp.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/wei/goods")
@RestController
@AllArgsConstructor
public class WeiGoodsController extends BaseController {
    private final OmsWeiGoodsSkuService skuService;
    private final OGoodsSkuService oGoodsSkuService;

    @RequestMapping(value = "/skuList", method = RequestMethod.GET)
    public TableDataInfo skuList(WeiGoodsSku bo, PageQuery pageQuery) {
        PageResult<WeiGoodsSku> result = skuService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    /**
     *
     */
    @GetMapping(value = "/sku/{id}")
    public AjaxResult getSkuInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(skuService.getById(id));
    }
    @PostMapping(value = "/sku/linkErp")
    public AjaxResult linkErp(@RequestBody LinkErpGoodsSkuBo bo)
    {
        if(StringUtils.isBlank(bo.getId())){
            return AjaxResult.error(500,"缺少参数Id");
        }
        if(StringUtils.isBlank(bo.getErpGoodsSkuId())){
            return AjaxResult.error(500,"缺少参数oGoodsSkuId");
        }
        ResultVo resultVo = skuService.linkErpGoodsSku(bo);
        if(resultVo.getCode()==0)
            return success();
        else return AjaxResult.error(resultVo.getMsg());
    }
}
