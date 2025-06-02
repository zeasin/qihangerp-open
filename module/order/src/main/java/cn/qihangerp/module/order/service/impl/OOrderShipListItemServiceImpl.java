package cn.qihangerp.module.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.module.order.domain.OOrderShipListItem;
import cn.qihangerp.module.order.service.OOrderShipListItemService;
import cn.qihangerp.module.order.mapper.OOrderShipListItemMapper;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【o_order_ship_list_item(发货-备货表（打单加入备货清单）)】的数据库操作Service实现
* @createDate 2025-06-01 23:07:24
*/
@Service
public class OOrderShipListItemServiceImpl extends ServiceImpl<OOrderShipListItemMapper, OOrderShipListItem>
    implements OOrderShipListItemService{

}




