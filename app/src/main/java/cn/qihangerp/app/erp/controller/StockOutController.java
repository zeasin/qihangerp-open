package cn.qihangerp.app.erp.controller;

import cn.qihangerp.app.security.common.BaseController;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.TableDataInfo;
import cn.qihangerp.domain.OGoodsSku;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/erp-api/stockOut")
public class StockOutController extends BaseController {
    @GetMapping("/list")
    public TableDataInfo list(OGoodsSku bo, PageQuery pageQuery)
    {
//        var pageList = goodsService.querySkuPageList(bo,pageQuery);
        return getDataTable(new PageResult<>());
    }
}
