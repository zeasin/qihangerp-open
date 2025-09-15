package cn.qihangerp.module.order.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.model.entity.ErpShipmentItem;
import cn.qihangerp.module.order.mapper.ErpShipmentItemMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.model.entity.ErpShipment;
import cn.qihangerp.module.order.service.ErpShipmentService;
import cn.qihangerp.module.order.mapper.ErpShipmentMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
* @author qilip
* @description 针对表【erp_shipment(发货记录表)】的数据库操作Service实现
* @createDate 2025-06-01 23:22:40
*/
@AllArgsConstructor
@Service
public class ErpShipmentServiceImpl extends ServiceImpl<ErpShipmentMapper, ErpShipment>
    implements ErpShipmentService{
    private final ErpShipmentItemMapper shipmentItemMapper;
    @Override
    public PageResult<ErpShipment> queryPageList(ErpShipment shipping, PageQuery pageQuery) {
        LambdaQueryWrapper<ErpShipment> queryWrapper = new LambdaQueryWrapper<ErpShipment>()
                .eq(shipping.getShipper()!=null,ErpShipment::getShipper,shipping.getShipper())
                .eq(StringUtils.hasText(shipping.getOrderNum()), ErpShipment::getOrderNum, shipping.getOrderNum())
                .eq(StringUtils.hasText(shipping.getShipCode()), ErpShipment::getShipCode, shipping.getShipCode())
                .eq(shipping.getShopId() != null, ErpShipment::getShopId, shipping.getShopId());

        Page<ErpShipment> pages = this.baseMapper.selectPage(pageQuery.build(), queryWrapper);
        if(pages.getRecords().size()>0){
            for(ErpShipment item : pages.getRecords()){
                item.setItemList(shipmentItemMapper.selectList(new LambdaQueryWrapper<ErpShipmentItem>()
                        .eq(ErpShipmentItem::getShipmentId,item.getId())));
            }
        }
        return PageResult.build(pages);
    }

    @Override
    public ErpShipment queryDetailById(Long id) {
        ErpShipment erpShipment = this.baseMapper.selectById(id);
        if(erpShipment!=null){
            erpShipment.setItemList(shipmentItemMapper.selectList(new LambdaQueryWrapper<ErpShipmentItem>().eq(ErpShipmentItem::getShipmentId,erpShipment.getId())));
        }
        return erpShipment;
    }
}




