package cn.qihangerp.open.dou.service;

import cn.qihangerp.common.PageQuery;
import cn.qihangerp.common.PageResult;

import cn.qihangerp.open.dou.domain.DouGoodsSku;
import cn.qihangerp.open.dou.domain.bo.DouGoodsBo;
import cn.qihangerp.open.dou.domain.vo.DouGoodsSkuListVo;
import com.baomidou.mybatisplus.extension.service.IService;


/**
* @author TW
* @description 针对表【dou_goods_sku(抖店商品Sku表)】的数据库操作Service
* @createDate 2024-05-31 17:23:21
*/
public interface DouGoodsSkuService extends IService<DouGoodsSku> {
    PageResult<DouGoodsSkuListVo> queryPageList(DouGoodsBo bo, PageQuery pageQuery);
}
