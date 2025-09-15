package cn.qihangerp.module.stock.request;

import lombok.Data;

import java.util.List;

@Data
public class StockOutCreateRequest {
    private String outNum;
    private Integer type;
    private String sourceNo;
    private String operator;
    private String remark;
    private List<GoodsSkuInventoryVo> itemList;
}
