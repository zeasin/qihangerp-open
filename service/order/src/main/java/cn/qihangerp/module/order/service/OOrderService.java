package cn.qihangerp.module.order.service;


import cn.qihangerp.module.order.domain.OOrder;
import cn.qihangerp.module.order.domain.OOrderItem;
import cn.qihangerp.module.order.domain.bo.OrderAllocateShipRequest;
import cn.qihangerp.module.order.domain.bo.OrderShipRequest;
import cn.qihangerp.module.order.domain.vo.SalesDailyVo;
import cn.qihangerp.model.request.OrderSearchRequest;
import com.baomidou.mybatisplus.extension.service.IService;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;



import java.util.List;

/**
* @author qilip
* @description 针对表【o_order(订单表)】的数据库操作Service
* @createDate 2024-03-09 13:15:57
*/
public interface OOrderService extends IService<OOrder> {

    PageResult<OOrder> queryPageList(OrderSearchRequest bo, PageQuery pageQuery);
    /**
     * 获取待发货list（去除处理过的）
     * @param bo
     * @param pageQuery
     * @return
     */
    PageResult<OOrder> queryWaitShipmentPageList(OrderSearchRequest bo, PageQuery pageQuery);

    /**
     * 获取已分配发货的list
     * @param bo
     * @param pageQuery
     * @return
     */
    PageResult<OOrder> queryAssignedShipmentList(OrderSearchRequest bo, PageQuery pageQuery);


    /**
     * 已经发货的list
     * @param bo
     * @param pageQuery
     * @return
     */
    PageResult<OOrder> queryShippedPageList(OrderSearchRequest bo, PageQuery pageQuery);

    OOrder queryDetailById(Long id);

    List<OOrder> searchOrderConsignee(String consignee);
    List<OOrderItem> searchOrderItemByReceiverMobile(String receiverMobile);

    /**
     * 手动添加订单
     * @param bo
     * @return
     */
//    int insertErpOrder(OrderCreateBo bo,String createBy);

    List<SalesDailyVo> salesDaily();
    SalesDailyVo getTodaySalesDaily();
    Integer getWaitShipOrderAllCount();
    /**
     * 手动发货
     * @param shipBo
     * @return
     */
    ResultVo<Integer> manualShipmentOrder(OrderShipRequest shipBo, String createBy);

    /**
     * 分配给供应商发货
     * @param shipBo
     * @param createBy
     * @return
     */
    ResultVo<Integer> allocateShipmentOrder(OrderAllocateShipRequest shipBo, String createBy);
}
