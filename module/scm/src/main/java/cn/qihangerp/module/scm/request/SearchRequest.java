package cn.qihangerp.module.scm.request;

import lombok.Data;

@Data
public class SearchRequest {
    // 供应商id
    private Integer supplierId;
    private String orderNum;
    private String orderStatus;
    private String startTime;
    private String endTime;


}
