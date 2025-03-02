package cn.qihangerp.api.stock.controller;//package cn.qihangerp.app.erp.controller;
//
//import cn.qihangerp.app.security.common.BaseController;
//import cn.qihangerp.common.TableDataInfo;
//import cn.qihangerp.domain.OGoodsSku;
//import cn.qihangerp.module.service.OGoodsSkuService;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@AllArgsConstructor
//@RestController
//@RequestMapping("/erp-api/goods")
//public class GoodsSkuController extends BaseController {
//    private final OGoodsSkuService skuService;
//    /**
//     * 搜索商品SKU
//     * 条件：商品编码、SKU、商品名称
//     */
//    @GetMapping("/searchSku")
//    public TableDataInfo searchSkuBy(String keyword)
//    {
//        logger.info("========SKU搜索=========",keyword);
//        List<OGoodsSku> list = skuService.searchGoodsSpec(keyword);
//        return getDataTable(list);
//    }
//}
