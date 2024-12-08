package cn.qihangerp.module.scm.request;


import java.util.List;

/**
 * 采购订单对象 scm_purchase_order
 * 
 * @author qihang
 * @date 2023-12-29
 */
public class PurchaseOrderStockInBo
{
    private static final long serialVersionUID = 1L;

    private Long id;//采购单id

    private String createBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    private List<PurchaseOrderStockInItemBo> goodsList;

    public List<PurchaseOrderStockInItemBo> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<PurchaseOrderStockInItemBo> goodsList) {
        this.goodsList = goodsList;
    }

}
