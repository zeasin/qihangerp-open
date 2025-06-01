package cn.qihangerp.module.order.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.module.order.domain.OShipment;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author qilip
* @description 针对表【o_order_shipping(发货记录表)】的数据库操作Service
* @createDate 2024-03-31 11:16:00
*/
public interface OShipmentService extends IService<OShipment> {
    /**
     * 发货记录
     * @param shipping
     * @param pageQuery
     * @return
     */
    PageResult<OShipment> queryPageList(OShipment shipping, PageQuery pageQuery);


    /**
     * 发货消息
     * @param orderNum
     * @param shopType
     * @param logisticsCompany
     * @param logisticsCode
     * @return
     */
//    ResultVo<Integer> shipSendMessage(String orderNum, EnumShopType shopType,String logisticsCompany,String logisticsCode);
}
