//package cn.qihangerp.api.oms.controller;
//
//import cn.qihangerp.common.AjaxResult;
//import cn.qihangerp.domain.OLogisticsCompany;
//import cn.qihangerp.module.service.OLogisticsCompanyService;
//import cn.qihangerp.security.common.BaseController;
//import lombok.AllArgsConstructor;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.Arrays;
//
//@AllArgsConstructor
//@RestController
//@RequestMapping("/api/oms-api/logistics")
//public class LogisticsController extends BaseController {
//    private final OLogisticsCompanyService logisticsCompanyService;
//    /**
//     * 获取物流公司详细信息
//     */
//    @GetMapping(value = "/{id}")
//    public AjaxResult getInfo(@PathVariable("id") Long id)
//    {
//        return success(logisticsCompanyService.getById(id));
//    }
//
//    /**
//     * 新增物流公司
//     */
//    @PostMapping
//    public AjaxResult add(@RequestBody OLogisticsCompany bLogisticsCompany)
//    {
//        return toAjax(logisticsCompanyService.save(bLogisticsCompany));
//    }
//
//    /**
//     * 修改物流公司
//     */
//    @PutMapping
//    public AjaxResult edit(@RequestBody OLogisticsCompany bLogisticsCompany)
//    {
//        return toAjax(logisticsCompanyService.updateById(bLogisticsCompany));
//    }
//
//    /**
//     * 删除物流公司
//     */
//    @DeleteMapping("/{ids}")
//    public AjaxResult remove(@PathVariable Long[] ids)
//    {
//        return toAjax(logisticsCompanyService.removeBatchByIds(Arrays.stream(ids).toList()));
//    }
//}
