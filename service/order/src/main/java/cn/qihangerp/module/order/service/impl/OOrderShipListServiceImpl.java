package cn.qihangerp.module.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.OOrderShipList;
import cn.qihangerp.module.order.service.OOrderShipListService;
import cn.qihangerp.module.order.mapper.OOrderShipListMapper;
import org.springframework.stereotype.Service;

/**
* @author qilip
* @description 针对表【o_order_ship_list(发货-备货表（取号发货加入备货清单、分配供应商发货加入备货清单）)】的数据库操作Service实现
* @createDate 2025-06-01 23:07:24
*/
@Service
public class OOrderShipListServiceImpl extends ServiceImpl<OOrderShipListMapper, OOrderShipList>
    implements OOrderShipListService{

}




