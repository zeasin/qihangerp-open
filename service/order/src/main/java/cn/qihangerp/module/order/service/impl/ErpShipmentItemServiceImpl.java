package cn.qihangerp.module.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.ErpShipmentItem;
import cn.qihangerp.module.order.service.ErpShipmentItemService;
import cn.qihangerp.module.order.mapper.ErpShipmentItemMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【erp_shipment_item(发货明细表)】的数据库操作Service实现
* @createDate 2025-06-01 23:25:11
*/
@AllArgsConstructor
@Service
public class ErpShipmentItemServiceImpl extends ServiceImpl<ErpShipmentItemMapper, ErpShipmentItem>
    implements ErpShipmentItemService{

}




