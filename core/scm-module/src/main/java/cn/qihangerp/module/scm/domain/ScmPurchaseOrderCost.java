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
 * 采购订单费用确认表
 * @TableName scm_purchase_order_cost
 */
@TableName(value ="scm_purchase_order_cost")
@Data
public class ScmPurchaseOrderCost implements Serializable {
    /**
     * 采购单ID（主键）
     */
    @TableId
    private Long id;

    /**
     * 
     */
    private Long supplierId;

    /**
     * 
     */
    private Long orderId;

    /**
     * 采购单金额
     */
    private BigDecimal orderAmount;

    /**
     * 采购订单日期
     */
    private Date orderDate;

    /**
     * 采购订单编号
     */
    private String orderNum;

    /**
     * 采购订单商品规格数
     */
    private Integer orderSpecUnit;

    /**
     * 采购订单商品数
     */
    private Integer orderGoodsUnit;

    /**
     * 采购订单总件数
     */
    private Integer orderSpecUnitTotal;

    /**
     * 实际金额
     */
    private BigDecimal actualAmount;

    /**
     * 运费
     */
    private BigDecimal freight;

    /**
     * 确认人
     */
    private String confirmUser;

    /**
     * 确认时间
     */
    private Date confirmTime;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 已支付金额
     */
    private BigDecimal payAmount;

    /**
     * 支付时间
     */
    private Date payTime;

    /**
     * 支付次数
     */
    private Integer payCount;

    /**
     * 说明
     */
    private String remark;

    /**
     * 状态（0未支付1已支付）
     */
    private Integer status;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}