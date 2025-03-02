package cn.qihangerp.module.scm.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.Data;

/**
 * 采购单应付款
 * @TableName scm_purchase_order_payable
 */
@TableName(value ="scm_purchase_order_payable")
@Data
public class ScmPurchaseOrderPayable implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 供应商id
     */
    private Long supplierId;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 应付金额
     */
    private BigDecimal amount;

    /**
     * 应付日期
     */
    private Date date;

    /**
     * 发票号码
     */
    private String invoiceNo;

    /**
     * 采购单号
     */
    private String purchaseOrderNum;

    /**
     * 采购说明
     */
    private String purchaseDesc;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态（0已生成1已结算)
     */
    private Integer status;

    /**
     * 订单创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 更新人
     */
    private String updateBy;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}