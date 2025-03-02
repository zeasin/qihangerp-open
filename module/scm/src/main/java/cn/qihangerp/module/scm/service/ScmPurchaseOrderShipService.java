package cn.qihangerp.module.scm.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.scm.domain.ScmPurchaseOrderShip;
import cn.qihangerp.module.scm.request.PurchaseOrderStockInBo;
import cn.qihangerp.module.scm.request.SearchRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【scm_purchase_order_ship(采购订单物流表)】的数据库操作Service
* @createDate 2024-10-20 17:18:53
*/
public interface ScmPurchaseOrderShipService extends IService<ScmPurchaseOrderShip> {
    PageResult<ScmPurchaseOrderShip> queryPageList(SearchRequest bo, PageQuery pageQuery);

    int updateScmPurchaseOrderShip(ScmPurchaseOrderShip scmPurchaseOrderShip);

    int createStockInEntry(PurchaseOrderStockInBo bo);
}
