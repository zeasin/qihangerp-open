package cn.qihangerp.module.service.impl;

import cn.qihangerp.module.mapper.OShopPullLasttimeMapper;
import cn.qihangerp.module.service.OShopPullLasttimeService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.qihangerp.domain.OShopPullLasttime;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author TW
* @description 针对表【sys_shop_pull_lasttime(店铺更新最后时间记录)】的数据库操作Service实现
* @createDate 2024-06-04 16:13:03
*/
@AllArgsConstructor
@Service
public class OShopPullLasttimeServiceImpl extends ServiceImpl<OShopPullLasttimeMapper, OShopPullLasttime>
    implements OShopPullLasttimeService {
    private final OShopPullLasttimeMapper mapper;

    @Override
    public OShopPullLasttime getLasttimeByShop(Long shopId, String pullType) {
        List<OShopPullLasttime> oShopPullLasttimes = mapper.selectList(new LambdaQueryWrapper<OShopPullLasttime>().eq(OShopPullLasttime::getShopId, shopId).eq(OShopPullLasttime::getPullType, pullType));
        if(oShopPullLasttimes != null && !oShopPullLasttimes.isEmpty()) return oShopPullLasttimes.get(0);
        else return null;
    }
}




