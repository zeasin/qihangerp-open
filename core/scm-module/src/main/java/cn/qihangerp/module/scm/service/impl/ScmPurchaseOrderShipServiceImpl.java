package cn.qihangerp.module.scm.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.mq.MqMessage;
import cn.qihangerp.common.mq.MqType;
import cn.qihangerp.common.mq.MqUtils;
import cn.qihangerp.common.utils.DateUtils;
import cn.qihangerp.module.scm.domain.ScmPurchaseOrder;
import cn.qihangerp.module.scm.domain.ScmPurchaseOrderShip;
import cn.qihangerp.module.scm.mapper.ScmPurchaseOrderMapper;
import cn.qihangerp.module.scm.mapper.ScmPurchaseOrderShipMapper;
import cn.qihangerp.module.scm.request.PurchaseOrderStockInBo;
import cn.qihangerp.module.scm.request.SearchRequest;
import cn.qihangerp.module.scm.service.ScmPurchaseOrderShipService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* @author qilip
* @description 针对表【scm_purchase_order_ship(采购订单物流表)】的数据库操作Service实现
* @createDate 2024-10-20 17:18:53
*/
@AllArgsConstructor
@Service
public class ScmPurchaseOrderShipServiceImpl extends ServiceImpl<ScmPurchaseOrderShipMapper, ScmPurchaseOrderShip>
    implements ScmPurchaseOrderShipService {
    private final ScmPurchaseOrderShipMapper shipMapper;
    private final ScmPurchaseOrderMapper orderMapper;
    private final MqUtils mqUtils;
    private final String DATE_PATTERN =
            "^(?:(?:(?:\\d{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\\d|2[0-8]))|(?:(?:(?:\\d{2}(?:0[48]|[2468][048]|[13579][26])|(?:(?:0[48]|[2468][048]|[13579][26])00))-0?2-29))$)|(?:(?:(?:\\d{4}-(?:0?[13578]|1[02]))-(?:0?[1-9]|[12]\\d|30))$)|(?:(?:(?:\\d{4}-0?[13-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|30))$)|(?:(?:(?:\\d{2}(?:0[48]|[13579][26]|[2468][048])|(?:(?:0[48]|[13579][26]|[2468][048])00))-0?2-29))$)$";
    private final Pattern DATE_FORMAT = Pattern.compile(DATE_PATTERN);
    @Override
    public PageResult<ScmPurchaseOrderShip> queryPageList(SearchRequest bo, PageQuery pageQuery) {
        if(org.springframework.util.StringUtils.hasText(bo.getStartTime())){
            Matcher matcher = DATE_FORMAT.matcher(bo.getStartTime());
            boolean b = matcher.find();
            if(b){
                bo.setStartTime(bo.getStartTime()+" 00:00:00");
            }
        }
        if(org.springframework.util.StringUtils.hasText(bo.getEndTime())){
            Matcher matcher = DATE_FORMAT.matcher(bo.getEndTime());
            boolean b = matcher.find();
            if(b){
                bo.setEndTime(bo.getEndTime()+" 23:59:59");
            }
        }

        LambdaQueryWrapper<ScmPurchaseOrderShip> queryWrapper = new LambdaQueryWrapper<ScmPurchaseOrderShip>()
                .eq(bo.getSupplierId()!=null,ScmPurchaseOrderShip::getSupplierId,bo.getSupplierId())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),ScmPurchaseOrderShip::getOrderNum,bo.getOrderNum())
                .eq(bo.getOrderStatus()!=null,ScmPurchaseOrderShip::getStatus,bo.getOrderStatus())
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),ScmPurchaseOrderShip::getShipTime,bo.getStartTime()+" 00:00:00")
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),ScmPurchaseOrderShip::getShipTime,bo.getEndTime()+" 23:59:59")
                ;


        Page<ScmPurchaseOrderShip> pages = shipMapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(pages);
    }

    @Transactional
    @Override
    public int updateScmPurchaseOrderShip(ScmPurchaseOrderShip scmPurchaseOrderShip)
    {
        ScmPurchaseOrderShip ship = shipMapper.selectById(scmPurchaseOrderShip.getId());
        if(ship== null) return -1;
        else if(ship.getStatus()!=0)return -2;
        // 更新采购单状态
        ScmPurchaseOrder order = new ScmPurchaseOrder();
        order.setId(scmPurchaseOrderShip.getOrderId());
        order.setStatus(2);
        order.setReceivedTime(scmPurchaseOrderShip.getReceiptTime());
        order.setUpdateTime(DateUtils.getNowDate());
        order.setUpdateBy(scmPurchaseOrderShip.getUpdateBy());
        orderMapper.updateById(order);
        //更新
        ScmPurchaseOrderShip update = new ScmPurchaseOrderShip();
        update.setId(ship.getId());
        update.setUpdateTime(DateUtils.getNowDate());
        update.setUpdateBy(scmPurchaseOrderShip.getUpdateBy());
        update.setStatus(1);
        update.setRemark(scmPurchaseOrderShip.getRemark());
        update.setReceiptTime(scmPurchaseOrderShip.getReceiptTime());
//        update.setReceiptTime(DateUtils.getNowDate());
        update.setId(scmPurchaseOrderShip.getId());
        return shipMapper.updateById(update);
    }

    @Transactional
    @Override
    public int createStockInEntry(PurchaseOrderStockInBo bo) {
        ScmPurchaseOrderShip ship = shipMapper.selectById(bo.getId());
        if(ship == null) return -1;//数据不存在
        else if(ship.getStatus().intValue() == 0) return -2;//未确认收货不允许操作
        else if(ship.getStatus().intValue() == 2) return -3;//已入库请勿重复操作
        else if (ship.getStatus().intValue() == 1) {
//            mqUtils.sendApiMessage(MqMessage.build(null, MqType.PURCHASE_STOCK_IN,"0"));
//            WmsStockInEntry entry = new WmsStockInEntry();
//            entry.setStockInNum(DateUtils.parseDateToStr("yyyyMMddHHmmss",new Date()));
//            entry.setSourceId(ship.getId());
//            entry.setSourceNo(ship.getOrderNo());
//            entry.setSourceSpecUnit(ship.getOrderSpecUnit());
//            entry.setSourceGoodsUnit(ship.getOrderGoodsUnit());
//            entry.setSourceSpecUnitTotal(ship.getOrderSpecUnitTotal());
//            entry.setSourceType(1);
//            entry.setStatus(0);
//            entry.setCreateBy(bo.getCreateBy());
//            entry.setCreateTime(new Date());
//            stockInEntryMapper.insert(entry);
//
//            // 子表
//
//            if(bo.getGoodsList()!=null && bo.getGoodsList().size()>0){
//                List<WmsStockInEntryItem> items = new ArrayList<>();
//                for (var item:bo.getGoodsList()) {
//                    WmsStockInEntryItem entryItem = new WmsStockInEntryItem();
//                    entryItem.setEntryId(entry.getId());
//                    entryItem.setSourceType(1);
//                    entryItem.setSourceId(ship.getId());
//                    entryItem.setSourceItemId(item.getId());
//                    entryItem.setGoodsId(item.getGoodsId());
//                    entryItem.setGoodsNum(item.getGoodsNum());
//                    entryItem.setGoodsName(item.getGoodsName());
//                    entryItem.setSpecId(item.getSpecId());
//                    entryItem.setSpecNum(item.getSpecNum());
//                    entryItem.setColorValue(item.getColorValue());
//                    entryItem.setColorImage(item.getColorImage());
//                    entryItem.setSizeValue(item.getSizeValue());
//                    entryItem.setStyleValue(item.getStyleValue());
//                    entryItem.setOriginalQuantity(item.getQuantity());
//                    entryItem.setInQuantity(0L);
//                    entryItem.setCreateBy(bo.getCreateBy());
//                    entryItem.setCreateTime(new Date());
//                    entryItem.setRemark("");
////                    entryItem.setLocationId(0L);
//                    entryItem.setStatus(0);
//                    items.add(entryItem);
//                }
////                stockInEntryMapper.batchWmsStockInEntryItem(items);
//                stockInEntryItemService.saveBatch(items);
//            }
//
//            // 更新表状态
//            ScmPurchaseOrderShip update = new ScmPurchaseOrderShip();
//            update.setUpdateTime(DateUtils.getNowDate());
//            update.setStockInTime(DateUtils.getNowDate());
//            update.setUpdateBy(bo.getCreateBy());
//            update.setStatus(2L);
//            update.setId(ship.getId());
//            scmPurchaseOrderShipMapper.updateScmPurchaseOrderShip(update);
//
//            //更新 采购订单
//            // 更新采购单状态
//            ScmPurchaseOrder order = new ScmPurchaseOrder();
//            order.setId(bo.getId());
//            order.setStatus(3);
//            order.setStockInTime(DateUtils.getNowDate());
//            order.setUpdateTime(DateUtils.getNowDate());
//            order.setUpdateBy(bo.getUpdateBy());
//            purchaseOrderMapper.updateScmPurchaseOrder(order);

            return 1;
        }
        else return -4;
    }
}




