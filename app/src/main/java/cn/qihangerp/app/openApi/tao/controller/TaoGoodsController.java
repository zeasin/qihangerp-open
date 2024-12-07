package cn.qihangerp.app.openApi.tao.controller;


import cn.qihangerp.app.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.domain.OGoodsSku;
import cn.qihangerp.domain.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.module.service.OGoodsSkuService;
import cn.qihangerp.open.tao.domain.TaoGoods;
import cn.qihangerp.open.tao.domain.TaoGoodsSku;
import cn.qihangerp.open.tao.domain.bo.TaoGoodsBo;
import cn.qihangerp.open.tao.domain.vo.TaoGoodsSkuListVo;
import cn.qihangerp.open.tao.service.TaoGoodsService;
import cn.qihangerp.open.tao.service.TaoGoodsSkuService;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/open-api/tao/goods")
@RestController
@AllArgsConstructor
public class TaoGoodsController extends BaseController {
    private final TaoGoodsService goodsService;
    private final TaoGoodsSkuService skuService;
    private final OGoodsSkuService oGoodsSkuService;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(TaoGoodsBo bo, PageQuery pageQuery) {
        PageResult<TaoGoods> result = goodsService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    @RequestMapping(value = "/skuList", method = RequestMethod.GET)
    public TableDataInfo skuList(TaoGoodsBo bo, PageQuery pageQuery) {
        PageResult<TaoGoodsSkuListVo> result = skuService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }

    /**
     * 获取店铺订单详细信息
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
        OGoodsSku oGoodsSku = oGoodsSkuService.getById(bo.getErpGoodsSkuId());
        if(oGoodsSku == null) return AjaxResult.error(1500,"未找到系统商品sku");
        TaoGoodsSku sku = new TaoGoodsSku();
        sku.setId(bo.getId());
        sku.setOGoodsSkuId(bo.getErpGoodsSkuId());
        skuService.updateById(sku);
        return success();
    }

}
