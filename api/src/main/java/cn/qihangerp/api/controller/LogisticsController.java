//package cn.qihangerp.api.controller;
//
//import cn.qihangerp.common.AjaxResult;
//import cn.qihangerp.common.PageQuery;
//import cn.qihangerp.common.PageResult;
//import cn.qihangerp.common.TableDataInfo;
//import cn.qihangerp.model.entity.OLogisticsCompany;
//import cn.qihangerp.module.service.OLogisticsCompanyService;
//import cn.qihangerp.module.service.OShopPlatformService;
//import cn.qihangerp.module.service.OShopService;
//import cn.qihangerp.security.common.BaseController;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
///**
// * 店铺Controller
// *
// * @author qihang
// * @date 2023-12-31
// */
//@AllArgsConstructor
//@RestController
//@RequestMapping("/shop")
//public class LogisticsController extends BaseController {
//    private final OLogisticsCompanyService logisticsCompanyService;
//    private final OShopService shopService;
//    private final OShopPlatformService platformService;
//
//
//
//    @GetMapping("/logistics_status")
//    public TableDataInfo logisticsStatusList(Integer status, Integer shopType, Integer shopId)
//    {
//        if(status==null) status=1;
//        if(shopType==null)shopType=0;
//        return getDataTable(logisticsCompanyService.queryListByStatus(status,shopType, shopId));
//    }
//    /**
//     * 查询店铺列表logistics
//     */
//    @GetMapping("/logistics")
//    public TableDataInfo logisticsList(Integer type, Integer shopId, PageQuery pageQuery)
//    {
//        if(type==null)type=0;
//        PageResult<OLogisticsCompany> result = logisticsCompanyService.queryPageList(type, shopId, pageQuery);
//        return getDataTable(result);
//    }
//
//    @PostMapping("/logistics/add")
//    public AjaxResult add(@RequestBody OLogisticsCompany company)
//    {
//        company.setPlatformId(0);
//        return toAjax(logisticsCompanyService.save(company));
//    }
//}
