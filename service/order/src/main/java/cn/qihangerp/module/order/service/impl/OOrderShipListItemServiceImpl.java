package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.bo.ShipStockUpBo;
import cn.qihangerp.model.bo.ShipStockUpCompleteBo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.OOrderShipListItem;
import cn.qihangerp.module.order.service.OOrderShipListItemService;
import cn.qihangerp.module.order.mapper.OOrderShipListItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
* @author qilip
* @description 针对表【o_order_ship_list_item(发货-备货表（打单加入备货清单）)】的数据库操作Service实现
* @createDate 2025-06-01 23:07:24
*/
@Service
public class OOrderShipListItemServiceImpl extends ServiceImpl<OOrderShipListItemMapper, OOrderShipListItem>
    implements OOrderShipListItemService{

    @Override
    public PageResult<OOrderShipListItem> queryPageList(ShipStockUpBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OOrderShipListItem> queryWrapper = new LambdaQueryWrapper<OOrderShipListItem>()
                .eq(bo.getShopId()!=null,OOrderShipListItem::getShopId,bo.getShopId())
                .eq(bo.getStatus()!=null,OOrderShipListItem::getStatus,bo.getStatus())
                .eq(StringUtils.hasText(bo.getOrderNum()),OOrderShipListItem::getOrderNum,bo.getOrderNum())
                ;
        Page<OOrderShipListItem> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }

    /**
     * 备货完成
     * @param bo
     * @return
     */
    @Override
    public int stockUpComplete(ShipStockUpCompleteBo bo) {

        if(bo.getIds() == null || bo.getIds().length == 0) return -1;

        int total=0;
        // 循环判断状态
        for (Long id:bo.getIds()) {
            OOrderShipListItem up = this.baseMapper.selectById(id);
            if (up != null) {
                if (up.getStatus() == 0 || up.getStatus() == 1) {
                    OOrderShipListItem update = new OOrderShipListItem();
                    update.setId(id);
                    update.setStatus(2);//备货完成
                    update.setUpdateBy("备货完成");
                    update.setUpdateTime(new Date());
                    this.baseMapper.updateById(update);
                }
            }
        }

        return 1;
    }

    /**
     * 备货完成 by Order
     * @param bo
     * @return
     */
    @Override
    public int stockUpCompleteByOrder(ShipStockUpCompleteBo bo) {

        if(bo.getOrderNums() == null || bo.getOrderNums().length == 0) return -1;

        int total=0;
        // 循环判断状态
        for (String orderNum:bo.getOrderNums()) {
            List<OOrderShipListItem> upList = this.baseMapper.selectList(new LambdaQueryWrapper<OOrderShipListItem>()
                    .eq(OOrderShipListItem::getOrderNum,orderNum));
            if (upList != null) {
                for(OOrderShipListItem up : upList) {
                    if (up.getStatus() == 0 || up.getStatus() == 1) {
                        OOrderShipListItem update = new OOrderShipListItem();
                        update.setId(up.getId());
                        update.setStatus(2);//备货完成
                        update.setUpdateBy("备货完成");
                        update.setUpdateTime(new Date());
                        this.baseMapper.updateById(update);
                    }
                }
            }
        }

        return 1;
    }
}




