package cn.qihangerp.module.order.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.ErpShipment;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【erp_shipment(发货记录表)】的数据库操作Service
* @createDate 2025-06-01 23:22:40
*/
public interface ErpShipmentService extends IService<ErpShipment> {
    PageResult<ErpShipment> queryPageList(ErpShipment shipping, PageQuery pageQuery);
    ErpShipment queryDetailById(Long id);
}
