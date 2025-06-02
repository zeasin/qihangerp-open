package cn.qihangerp.module.order.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.module.order.domain.OOrderShipListItem;
import cn.qihangerp.module.order.domain.bo.ShipStockUpBo;
import cn.qihangerp.module.order.domain.bo.ShipStockUpCompleteBo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author qilip
* @description 针对表【o_order_ship_list_item(发货-备货表（打单加入备货清单）)】的数据库操作Service
* @createDate 2025-06-01 23:07:24
*/
public interface OOrderShipListItemService extends IService<OOrderShipListItem> {
    PageResult<OOrderShipListItem> queryPageList(ShipStockUpBo bo, PageQuery pageQuery);

    /**
     * 备货完成
     * @param bo
     * @return
     */
    int stockUpComplete(ShipStockUpCompleteBo bo);
    int stockUpCompleteByOrder(ShipStockUpCompleteBo bo);
}
