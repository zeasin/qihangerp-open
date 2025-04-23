package cn.qihangerp.api.jd.controller.feign;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.module.open.jd.domain.JdRefund;
import cn.qihangerp.module.open.jd.domain.JdVcRefund;
import cn.qihangerp.module.open.jd.service.JdRefundService;
import cn.qihangerp.module.open.jd.service.JdVcRefundService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/jd/refund")
public class JdRefundFeignController {
    private final JdVcRefundService jdVcRefundService;
    private final JdRefundService jdRefundService;

    @GetMapping(value = "/get_detail")
    public AjaxResult getInfo(Long refundId,Integer vc)
    {
        if(vc!=null && vc==1) {
            // jdvc
            JdVcRefund refund = jdVcRefundService.getById(refundId);
            if (refund == null) return AjaxResult.error(404, "没有找到退款单");
            else return AjaxResult.success(refund);
        }else {
            //jd pop
            List<JdRefund> list = jdRefundService.list(new LambdaQueryWrapper<JdRefund>().eq(JdRefund::getServiceId, refundId));
            if(list.isEmpty()) return AjaxResult.error(404,"没有找到售后单");
            else return AjaxResult.success(list.get(0));
        }
    }
}
