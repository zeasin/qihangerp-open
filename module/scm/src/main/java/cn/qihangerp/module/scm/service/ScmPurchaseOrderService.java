package cn.qihangerp.module.scm.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;

import cn.qihangerp.module.scm.domain.ScmPurchaseOrder;
import cn.qihangerp.module.scm.request.PurchaseOrderAddRequest;
import cn.qihangerp.module.scm.request.PurchaseOrderOptionRequest;
import cn.qihangerp.module.scm.request.SearchRequest;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【scm_purchase_order(采购订单)】的数据库操作Service
* @createDate 2024-10-20 15:36:33
*/
public interface ScmPurchaseOrderService extends IService<ScmPurchaseOrder> {
    PageResult<ScmPurchaseOrder> queryPageList(SearchRequest bo, PageQuery pageQuery);
    ScmPurchaseOrder getDetailById(Long id);
    int createPurchaseOrder(PurchaseOrderAddRequest addBo);
    int updateScmPurchaseOrder(PurchaseOrderOptionRequest request);
}
