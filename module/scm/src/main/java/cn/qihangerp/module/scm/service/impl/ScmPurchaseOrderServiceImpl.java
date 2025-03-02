package cn.qihangerp.module.scm.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.utils.DateUtils;

import cn.qihangerp.module.scm.domain.*;
import cn.qihangerp.module.scm.mapper.*;
import cn.qihangerp.module.scm.request.PurchaseOrderAddRequest;
import cn.qihangerp.module.scm.request.PurchaseOrderOptionRequest;
import cn.qihangerp.module.scm.request.SearchRequest;
import cn.qihangerp.module.scm.service.ScmPurchaseOrderService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
* @author qilip
* @description 针对表【scm_purchase_order(采购订单)】的数据库操作Service实现
* @createDate 2024-10-20 15:36:33
*/
@AllArgsConstructor
@Service
public class ScmPurchaseOrderServiceImpl extends ServiceImpl<ScmPurchaseOrderMapper, ScmPurchaseOrder>
    implements ScmPurchaseOrderService {
    private final ScmPurchaseOrderMapper scmPurchaseOrderMapper;
    private final ScmPurchaseOrderItemMapper scmPurchaseOrderItemMapper;
    private final ScmPurchaseOrderCostMapper costMapper;
    private final ScmPurchaseOrderShipMapper shipMapper;
    private final ScmPurchaseOrderPayableMapper payableMapper;
    private final String DATE_PATTERN =
            "^(?:(?:(?:\\d{4}-(?:0?[1-9]|1[0-2])-(?:0?[1-9]|1\\d|2[0-8]))|(?:(?:(?:\\d{2}(?:0[48]|[2468][048]|[13579][26])|(?:(?:0[48]|[2468][048]|[13579][26])00))-0?2-29))$)|(?:(?:(?:\\d{4}-(?:0?[13578]|1[02]))-(?:0?[1-9]|[12]\\d|30))$)|(?:(?:(?:\\d{4}-0?[13-9]|1[0-2])-(?:0?[1-9]|[1-2]\\d|30))$)|(?:(?:(?:\\d{2}(?:0[48]|[13579][26]|[2468][048])|(?:(?:0[48]|[13579][26]|[2468][048])00))-0?2-29))$)$";
    private final Pattern DATE_FORMAT = Pattern.compile(DATE_PATTERN);
    @Override
    public PageResult<ScmPurchaseOrder> queryPageList(SearchRequest bo, PageQuery pageQuery) {
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

        LambdaQueryWrapper<ScmPurchaseOrder> queryWrapper = new LambdaQueryWrapper<ScmPurchaseOrder>()
                .eq(bo.getSupplierId()!=null,ScmPurchaseOrder::getSupplierId,bo.getSupplierId())
                .eq(org.springframework.util.StringUtils.hasText(bo.getOrderNum()),ScmPurchaseOrder::getOrderNum,bo.getOrderNum())
                .eq(bo.getOrderStatus()!=null,ScmPurchaseOrder::getStatus,bo.getOrderStatus())
                .ge(org.springframework.util.StringUtils.hasText(bo.getStartTime()),ScmPurchaseOrder::getOrderTime,bo.getStartTime()+" 00:00:00")
                .le(org.springframework.util.StringUtils.hasText(bo.getEndTime()),ScmPurchaseOrder::getOrderTime,bo.getEndTime()+" 23:59:59")
                ;

        pageQuery.setOrderByColumn("order_time");
        pageQuery.setIsAsc("desc");
        Page<ScmPurchaseOrder> pages = scmPurchaseOrderMapper.selectPage(pageQuery.build(), queryWrapper);

        // 查询子订单
        if(pages.getRecords()!=null){
            for (var order:pages.getRecords()) {
                order.setItemList(scmPurchaseOrderItemMapper.selectList(new LambdaQueryWrapper<ScmPurchaseOrderItem>().eq(ScmPurchaseOrderItem::getOrderId, order.getId())));

            }
        }

        return PageResult.build(pages);
    }

    @Override
    public ScmPurchaseOrder getDetailById(Long id) {
        ScmPurchaseOrder order = scmPurchaseOrderMapper.selectById(id);
        if(order!=null){
            order.setItemList(scmPurchaseOrderItemMapper.selectList(new LambdaQueryWrapper<ScmPurchaseOrderItem>().eq(ScmPurchaseOrderItem::getOrderId, order.getId())));
            return order;
        }else
            return null;
    }

    @Override
    public int createPurchaseOrder(PurchaseOrderAddRequest addBo) {
        if(addBo.getGoodsList() == null || addBo.getGoodsList().isEmpty()) return -1;
        // 添加主表
        ScmPurchaseOrder scmPurchaseOrder = new ScmPurchaseOrder();
        scmPurchaseOrder.setOrderNum("PUR"+ DateUtils.parseDateToStr("yyyyMMddHHmmss",new Date()));
        scmPurchaseOrder.setOrderAmount(addBo.getOrderAmount());
        scmPurchaseOrder.setCreateTime(DateUtils.getNowDate());
        scmPurchaseOrder.setOrderDate(addBo.getOrderDate());
        scmPurchaseOrder.setSupplierId(addBo.getContactId());
        scmPurchaseOrder.setOrderTime(System.currentTimeMillis()/1000);
        scmPurchaseOrder.setCreateBy(addBo.getCreateBy());
        scmPurchaseOrder.setStatus(0);
        scmPurchaseOrder.setShipAmount(BigDecimal.ZERO);
        scmPurchaseOrderMapper.insert(scmPurchaseOrder);

        // 添加子表
        for (var item:addBo.getGoodsList()) {
            ScmPurchaseOrderItem orderItem = new ScmPurchaseOrderItem();
            orderItem.setOrderDate(addBo.getOrderDate());
            orderItem.setOrderId(scmPurchaseOrder.getId());
            orderItem.setOrderNum(scmPurchaseOrder.getOrderNum());
            orderItem.setAmount(item.getAmount().doubleValue());
            orderItem.setGoodsId(item.getGoodsId());
            orderItem.setGoodsNum(item.getNumber());
            orderItem.setIsDelete(0);
            orderItem.setPrice(item.getPurPrice());
            orderItem.setQuantity(item.getQty());
            orderItem.setSpecId(item.getSkuId());
            orderItem.setSpecNum(item.getSkuCode());
            orderItem.setStatus(0);
            orderItem.setTransType("Purchase");
            orderItem.setGoodsName(item.getGoodsName());
            orderItem.setColorValue(item.getColorValue());
            orderItem.setColorImage(item.getColorImage());
            orderItem.setSizeValue(item.getSizeValue());
            orderItem.setStyleValue(item.getStyleValue());

            scmPurchaseOrderItemMapper.insert(orderItem);
        }
        return 1;
    }

    @Override
    public int updateScmPurchaseOrder(PurchaseOrderOptionRequest request) {
        ScmPurchaseOrder order = scmPurchaseOrderMapper.selectById(request.getId());
        if(order == null) return -1;


        if(request.getOptionType().equals("audit")){
            if(order.getStatus() !=0){
                // 状态不是待审核的
                return -1;
            }
            ScmPurchaseOrder scmPurchaseOrder = new ScmPurchaseOrder();
            scmPurchaseOrder.setId(order.getId());
            scmPurchaseOrder.setUpdateBy(request.getUpdateBy());
            scmPurchaseOrder.setUpdateTime(DateUtils.getNowDate());
            scmPurchaseOrder.setAuditUser(request.getAuditUser());
            scmPurchaseOrder.setAuditTime(System.currentTimeMillis()/1000);
            scmPurchaseOrder.setRemark(request.getRemark());
            scmPurchaseOrder.setStatus(1);
            return scmPurchaseOrderMapper.updateById(scmPurchaseOrder);
        }
        else if (request.getOptionType().equals("confirm")) {
            if(order.getStatus() !=1){
                // 状态不是已审核的不能确认
                return -1;
            }
            // 查询数据
            List<ScmPurchaseOrderItem> items = scmPurchaseOrderItemMapper.selectList(new LambdaQueryWrapper<ScmPurchaseOrderItem>().eq(ScmPurchaseOrderItem::getOrderId, request.getId()));

            Map<Long, List<ScmPurchaseOrderItem>> goodsGroup = items.stream().collect(Collectors.groupingBy(x -> x.getGoodsId()));
            Long total = items.stream().mapToLong(ScmPurchaseOrderItem::getQuantity).sum();
            // 生成费用信息
            ScmPurchaseOrderCost cost = new ScmPurchaseOrderCost();
            cost.setId(order.getId());
            cost.setOrderId(order.getId());
            cost.setSupplierId(order.getSupplierId());
            cost.setOrderNum(order.getOrderNum());
            cost.setOrderDate(order.getOrderDate());
            cost.setOrderGoodsUnit(goodsGroup.size());
            cost.setOrderSpecUnit(items.size());
            cost.setOrderSpecUnitTotal(total.intValue());
            cost.setOrderAmount(order.getOrderAmount());
            cost.setActualAmount(request.getTotalAmount());
            cost.setFreight(BigDecimal.ZERO);
            cost.setConfirmUser(request.getConfirmUser());
            cost.setConfirmTime(new Date());
            cost.setCreateBy(request.getUpdateBy());
            cost.setPayAmount(BigDecimal.ZERO);
            cost.setPayCount(0);
            cost.setStatus(0);
            costMapper.insert(cost);

            // 更新主表
            ScmPurchaseOrder scmPurchaseOrder = new ScmPurchaseOrder();
            scmPurchaseOrder.setId(order.getId());
            scmPurchaseOrder.setUpdateBy(request.getUpdateBy());
            scmPurchaseOrder.setUpdateTime(DateUtils.getNowDate());
            scmPurchaseOrder.setStatus(101);
            scmPurchaseOrder.setSupplierConfirmTime(new Date());
            scmPurchaseOrderMapper.updateById(scmPurchaseOrder);
        }
        else if (request.getOptionType().equals("SupplierShip")) {
            if(order.getStatus() !=101){
                // 状态不是已确认的不能发货
                return -1;
            }
            // 查询数据
            List<ScmPurchaseOrderItem> items = scmPurchaseOrderItemMapper.selectList(new LambdaQueryWrapper<ScmPurchaseOrderItem>().eq(ScmPurchaseOrderItem::getOrderId, request.getId()));

            Map<Long, List<ScmPurchaseOrderItem>> goodsGroup = items.stream().collect(Collectors.groupingBy(x -> x.getGoodsId()));
            Long total = items.stream().mapToLong(ScmPurchaseOrderItem::getQuantity).sum();

            // 生成物流信息
            ScmPurchaseOrderShip ship = new ScmPurchaseOrderShip();
            ship.setId(order.getId());
            ship.setOrderId(order.getId());
            ship.setSupplierId(order.getSupplierId());
            ship.setOrderNum(order.getOrderNum());
            ship.setOrderDate(order.getOrderDate());
            ship.setOrderGoodsUnit(goodsGroup.size());
            ship.setOrderSpecUnit(items.size());
            ship.setOrderSpecUnitTotal(total.intValue());
            ship.setShipCompany(request.getShipCompany());
            ship.setShipNum(request.getShipNo());
            ship.setFreight(request.getShipCost());
            ship.setShipTime(request.getSupplierDeliveryTime());
            ship.setCreateBy(request.getUpdateBy());
            ship.setCreateTime(new Date());
            ship.setStatus(0);
            ship.setBackCount(0);
            ship.setStockInCount(0);
            shipMapper.insert(ship);
            // 更新费用表


//            ScmSupplier scmSupplier = supplierMapper.selectScmSupplierById(order.getContactId());
            // 生成应付信息fms_payable_purchase
            ScmPurchaseOrderPayable payable = new ScmPurchaseOrderPayable();
            payable.setSupplierId(order.getSupplierId());
//            fmsPP.setSupplierName(scmSupplier!=null ? scmSupplier.getName():"数据库未找到供应商信息");
            payable.setAmount(order.getOrderAmount().add(request.getShipCost()));
            payable.setDate(new Date());
            payable.setPurchaseOrderNum(order.getOrderNum());
            payable.setPurchaseDesc("{采购商品总数量:"+total+",不同款式:"+goodsGroup.size()+",不同SKU:"+items.size()+",商品总价:"+order.getOrderAmount()+",运费:"+request.getShipCost()+"}");
            payable.setStatus(0);
            payable.setCreateTime(new Date());
            payable.setCreateBy(request.getUpdateBy());
            payableMapper.insert(payable);

            // 更新主表
            ScmPurchaseOrder scmPurchaseOrder = new ScmPurchaseOrder();
            scmPurchaseOrder.setId(order.getId());
            scmPurchaseOrder.setUpdateBy(request.getUpdateBy());
            scmPurchaseOrder.setUpdateTime(DateUtils.getNowDate());
            scmPurchaseOrder.setStatus(102);
            scmPurchaseOrder.setSupplierDeliveryTime(new Date());
            scmPurchaseOrder.setShipAmount(request.getShipCost());
            scmPurchaseOrderMapper.updateById(scmPurchaseOrder);
        }
        return 1;
    }
}




