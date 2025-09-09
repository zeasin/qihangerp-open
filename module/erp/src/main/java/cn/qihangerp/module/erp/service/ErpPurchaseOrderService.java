package cn.qihangerp.module.erp.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.erp.bo.PurchaseOrderAddBo;
import cn.qihangerp.module.erp.bo.PurchaseOrderOptionBo;
import cn.qihangerp.module.erp.bo.SearchBo;
import cn.qihangerp.module.erp.domain.ErpPurchaseOrder;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 1
* @description 针对表【erp_purchase_order(采购订单)】的数据库操作Service
* @createDate 2025-09-09 09:51:48
*/
public interface ErpPurchaseOrderService extends IService<ErpPurchaseOrder> {
    PageResult<ErpPurchaseOrder> queryPageList(SearchBo bo, PageQuery pageQuery);
    ErpPurchaseOrder getDetailById(Long id);
    int createPurchaseOrder(PurchaseOrderAddBo addBo);
    int updateScmPurchaseOrder(PurchaseOrderOptionBo request);
}
