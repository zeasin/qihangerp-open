package cn.qihangerp.open.tao.service.impl;

import cn.qihangerp.open.tao.domain.bo.TaoGoodsBo;
import cn.qihangerp.open.tao.domain.vo.TaoGoodsSkuListVo;
import cn.qihangerp.open.tao.mapper.TaoGoodsSkuMapper;
import cn.qihangerp.open.tao.service.TaoGoodsSkuService;
import cn.qihangerp.open.tao.domain.TaoGoodsSku;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author TW
* @description 针对表【tao_goods_sku】的数据库操作Service实现
* @createDate 2024-02-29 19:01:35
*/
@AllArgsConstructor
@Service
public class TaoGoodsSkuServiceImpl extends ServiceImpl<TaoGoodsSkuMapper, TaoGoodsSku>
    implements TaoGoodsSkuService {
    private final TaoGoodsSkuMapper mapper;

    @Override
    public PageResult<TaoGoodsSkuListVo> queryPageList(TaoGoodsBo bo, PageQuery pageQuery) {
        IPage<TaoGoodsSkuListVo> result = mapper.selectSkuPageList(pageQuery.build(), bo.getShopId(),bo.getNumIid(),bo.getSkuId(),bo.getOuterId(), bo.getHasLink());
        return PageResult.build(result);
    }
}




