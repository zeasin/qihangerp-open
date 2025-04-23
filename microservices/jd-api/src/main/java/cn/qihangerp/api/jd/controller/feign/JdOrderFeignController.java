package cn.qihangerp.api.jd.controller.feign;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.module.open.jd.domain.JdOrder;
import cn.qihangerp.module.open.jd.domain.JdVcOrder;
import cn.qihangerp.module.open.jd.service.JdOrderService;
import cn.qihangerp.module.open.jd.service.JdVcOrderService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/jd/order")
public class JdOrderFeignController {
    private final JdOrderService jdOrderService;
    private final JdVcOrderService jdVcOrderService;
    @GetMapping(value = "/get_detail")
    public AjaxResult getInfo(Long orderId,Integer vc)
    {
        if(vc!=null && vc==1){
            // jdvc
            JdVcOrder jdVcOrder = jdVcOrderService.queryDetailByOrderId(orderId);
            if(jdVcOrder!=null) return AjaxResult.success(jdVcOrder);
            else return AjaxResult.error(500,"没有找到订单信息");
        }else {
            JdOrder order = jdOrderService.queryDetailByOrderId(orderId);
            if (order == null) return AjaxResult.error(404, "没有找到订单");
            else return AjaxResult.success(order);
        }
    }
}
