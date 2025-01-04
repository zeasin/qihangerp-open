package cn.qihangerp.app.openApi.jd.controller;


import cn.qihangerp.app.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.domain.OGoodsSku;
import cn.qihangerp.domain.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.module.service.OGoodsSkuService;
import cn.qihangerp.module.open.jd.domain.JdVcGoods;
import cn.qihangerp.module.open.jd.domain.bo.JdGoodsBo;
import cn.qihangerp.module.open.jd.service.JdVcGoodsService;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/open-api/jdvc/goods")
@RestController
@AllArgsConstructor
public class JdVcGoodsController extends BaseController {
    private final JdVcGoodsService goodsService;
    private final OGoodsSkuService oGoodsSkuService;
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public TableDataInfo goodsList(JdGoodsBo bo, PageQuery pageQuery) {
        PageResult<JdVcGoods> result = goodsService.queryPageList(bo, pageQuery);

        return getDataTable(result);
    }


    /**
     * 获取店铺订单详细信息
     */
    @GetMapping(value = "/{id}")
    public AjaxResult getSkuInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(goodsService.getById(id));
    }
    @PostMapping(value = "/linkErpSku")
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
        JdVcGoods goods = new JdVcGoods();
        goods.setId(bo.getId());
        goods.setOGoodsSkuId(bo.getErpGoodsSkuId());
        goodsService.updateById(goods);
        return success();
    }

}
