package cn.qihangerp.module.order.bo;

import lombok.Data;

import java.util.Date;

@Data
public class SupplierOrderShipBo {
    private Long id;
    private String logisticsCompany;
    private String logisticsCode;
    private Date shipTime;

}
