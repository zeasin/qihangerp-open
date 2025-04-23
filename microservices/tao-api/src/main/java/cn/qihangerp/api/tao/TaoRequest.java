package cn.qihangerp.api.tao;

public class TaoRequest {
    private Long shopId;//店铺Id
    private Long orderId;//订单id
    private Long refundId;
    private Integer updType;//更新类型0拉取新订单1更新订单

    private Integer pullType;//拉取类型：0或不传全量；1更新（用于拉取商品的条件）

    public Integer getPullType() {
        return pullType;
    }

    public void setPullType(Integer pullType) {
        this.pullType = pullType;
    }

    public Integer getUpdType() {
        return updType;
    }

    public void setUpdType(Integer updType) {
        this.updType = updType;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getShopId() {
        return shopId;
    }

    public void setShopId(Long shopId) {
        this.shopId = shopId;
    }

    public Long getRefundId() {
        return refundId;
    }

    public void setRefundId(Long refundId) {
        this.refundId = refundId;
    }
}
