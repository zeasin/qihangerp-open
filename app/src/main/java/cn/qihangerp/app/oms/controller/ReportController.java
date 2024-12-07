package cn.qihangerp.app.oms.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.module.order.domain.vo.SalesDailyVo;
import cn.qihangerp.module.order.domain.vo.SalesTopSkuVo;
import cn.qihangerp.module.order.service.OOrderItemService;
import cn.qihangerp.module.order.service.OOrderService;
import cn.qihangerp.module.service.OShopService;
import cn.qihangerp.app.security.common.BaseController;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/oms-api/report")
public class ReportController extends BaseController {
    private final OOrderService orderService;
    private final OOrderItemService orderItemService;
    private final OShopService shopService;
    @GetMapping("/todayDaily")
    public AjaxResult todayDaily()
    {
        Long shopCount = shopService.list().stream().count();
        Map<String,Double> result = new HashMap<>();
        result.put("inventory",1002.00);
        result.put("salesVolume",50332.00);
        result.put("orderCount",502.00);
        result.put("shopCount",shopCount.doubleValue());

        return AjaxResult.success(result);
    }


    @GetMapping("/salesDaily")
    public AjaxResult salesDaily()
    {
        List<SalesDailyVo> salesDailyVos = orderService.salesDaily();

        return AjaxResult.success(salesDailyVos);
    }

    @GetMapping("/salesTopSku")
    public AjaxResult salesTopSku()
    {
        List<SalesTopSkuVo> salesTopSkuVos = orderItemService.selectTopSku("2024-01-01", "2024-12-31");

        return AjaxResult.success(salesTopSkuVos);
    }
}
