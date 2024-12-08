package cn.qihangerp.app.erp.controller;


import cn.qihangerp.app.security.common.BaseController;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.module.scm.domain.ScmLogistics;
import cn.qihangerp.module.scm.service.ScmLogisticsService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 商品管理Controller
 * 
 * @author qihang
 * @date 2023-12-29
 */
@AllArgsConstructor
@RestController
@RequestMapping("/erp-api/scm/logistics")
public class PurchaseLogisticsController extends BaseController
{
    private final ScmLogisticsService logisticsService;
    /**
     *
     */
    @GetMapping("/list")
    public TableDataInfo list()
    {
        List<ScmLogistics> list = logisticsService.list(new LambdaQueryWrapper<ScmLogistics>().eq(ScmLogistics::getStatus, 1));
        return getDataTable(list);
    }

}
