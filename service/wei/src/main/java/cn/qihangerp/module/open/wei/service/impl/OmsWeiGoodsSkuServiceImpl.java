package cn.qihangerp.module.open.wei.service.impl;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;
import cn.qihangerp.common.ResultVo;
import cn.qihangerp.domain.bo.LinkErpGoodsSkuBo;
import cn.qihangerp.module.goods.domain.OGoods;
import cn.qihangerp.module.goods.domain.OGoodsSku;
import cn.qihangerp.module.goods.service.OGoodsService;
import cn.qihangerp.module.goods.service.OGoodsSkuService;
import cn.qihangerp.module.open.wei.domain.OmsWeiGoods;
import cn.qihangerp.module.open.wei.domain.OmsWeiGoodsSku;
import cn.qihangerp.module.open.wei.mapper.OmsWeiGoodsMapper;
import cn.qihangerp.module.open.wei.mapper.OmsWeiGoodsSkuMapper;
import cn.qihangerp.module.open.wei.service.OmsWeiGoodsSkuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
* @author TW
* @description 针对表【oms_wei_goods_sku】的数据库操作Service实现
* @createDate 2024-06-03 16:51:29
*/
@AllArgsConstructor
@Service
public class OmsWeiGoodsSkuServiceImpl extends ServiceImpl<OmsWeiGoodsSkuMapper, OmsWeiGoodsSku>
    implements OmsWeiGoodsSkuService {
    private final  OmsWeiGoodsSkuMapper mapper;
    private final OmsWeiGoodsMapper weiGoodsMapper;
    private final OGoodsSkuService oGoodsSkuService;
    private final OGoodsService oGoodsService;

    @Override
    public PageResult<OmsWeiGoodsSku> queryPageList(OmsWeiGoodsSku bo, PageQuery pageQuery) {
        LambdaQueryWrapper<OmsWeiGoodsSku> queryWrapper = new LambdaQueryWrapper<OmsWeiGoodsSku>()
                .eq(bo.getShopId()!=null,OmsWeiGoodsSku::getShopId,bo.getShopId())
                ;

        Page<OmsWeiGoodsSku> page = mapper.selectPage(pageQuery.build(), queryWrapper);

        return PageResult.build(page);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ResultVo linkErpGoodsSku(LinkErpGoodsSkuBo bo) {
        OGoodsSku oGoodsSku = oGoodsSkuService.getById(bo.getErpGoodsSkuId());
        if(oGoodsSku == null) return ResultVo.error("未找到系统商品sku");

        OGoods oGoods=oGoodsService.getById(oGoodsSku.getGoodsId());
        if(oGoods == null){
            return ResultVo.error("未找到系统商品");
        }

        OmsWeiGoodsSku taoGoodsSku = mapper.selectById(bo.getId());
        if(taoGoodsSku == null) {
            return ResultVo.error("WEI商品sku数据不存在");
        }
        List<OmsWeiGoods> jdGoods = weiGoodsMapper.selectList(new LambdaQueryWrapper<OmsWeiGoods>()
                .eq(OmsWeiGoods::getProductId, taoGoodsSku.getProductId()));
        if(jdGoods==null||jdGoods.size()==0){
            return ResultVo.error("WEI商品数据不存在");
        }

        OmsWeiGoodsSku sku = new OmsWeiGoodsSku();
        sku.setId(Long.parseLong(bo.getId()));
        sku.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        sku.setErpGoodsSkuId(Long.parseLong(oGoodsSku.getId()));
        mapper.updateById(sku);

        OmsWeiGoods goodsUp=new OmsWeiGoods();
        goodsUp.setId(jdGoods.get(0).getId());
        goodsUp.setErpGoodsId(Long.parseLong(oGoodsSku.getGoodsId()));
        weiGoodsMapper.updateById(goodsUp);
        return ResultVo.success();
    }
}




