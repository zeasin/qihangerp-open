package cn.qihangerp.app.openApi.tao.controller;

import cn.qihangerp.app.openApi.tao.TaoApiCommon;
import cn.qihangerp.app.openApi.tao.TaoRequest;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.ResultVoEnum;
import cn.qihangerp.common.api.ShopApiParams;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.domain.OShopPullLasttime;
import cn.qihangerp.domain.OShopPullLogs;
import cn.qihangerp.module.service.OShopPullLasttimeService;
import cn.qihangerp.module.service.OShopPullLogsService;

import cn.qihangerp.sdk.common.ApiResultVo;
import cn.qihangerp.sdk.tao.GoodsApiHelper;
import cn.qihangerp.module.open.tao.domain.TaoGoods;
import cn.qihangerp.module.open.tao.domain.TaoGoodsSku;
import cn.qihangerp.sdk.tao.response.TaoGoodsResponse;
import cn.qihangerp.module.open.tao.service.TaoGoodsService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequestMapping("/api/open-api/tao/goods")
@RestController
@AllArgsConstructor
public class TaoGoodsApiController  {
    private final TaoApiCommon taoApiCommon;
    private final TaoGoodsService goodsService;
    private final OShopPullLogsService pullLogsService;
    private final OShopPullLasttimeService pullLasttimeService;
/**
        * @api {post} /api/v1/pull_goods 更新店铺商品列表
     * @apiVersion 1.0.0
            * @apiName pullGoods
     * @apiGroup taoGood
     * @apiParam {String}  startTime 开始时间
     * @apiParam {String}  endTime 结束时间
     * @apiParam {Number}  shopId 店铺id(东方符号：7)
     * @apiSuccessExample {json} Success-Response:
            * HTTP/1.1 200 OK{
        "code": "0成功其他失败",
                "msg": "成功或失败信息"
    }
     */
    @RequestMapping(value = "/pull_goods", method = RequestMethod.POST)
    public AjaxResult pullGoodsList(@RequestBody TaoRequest req) throws Exception {
        if (req.getShopId() == null || req.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
//            return ApiResult.build(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        Date currDateTime = new Date();
        long startTimeMillis = System.currentTimeMillis();
        var checkResult = taoApiCommon.checkBefore(req.getShopId());
        if (checkResult.getCode() != HttpStatus.SUCCESS) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg());
        }
        ShopApiParams shopApiParams = checkResult.getData();
        String sessionKey = shopApiParams.getAccessToken();
        String url = shopApiParams.getServerUrl();
        String appKey = shopApiParams.getAppKey();
        String appSecret = shopApiParams.getAppSecret();

        // 获取最后更新时间
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        OShopPullLasttime lasttime = pullLasttimeService.getLasttimeByShop(req.getShopId(), "GOODS");
        if(req.getPullType()!=null && req.getPullType()==1) {
            if (lasttime != null) {
                // 按更新时间来
                startTime = lasttime.getLasttime().minusHours(7*24);//取上次结束一个小时前
                endTime = LocalDateTime.now();
            }
        }

        Long pageIndex = 1L;
        Long pageSize = 100L;

//        ApiResult<TaoGoods> listApiResult = GoodsApiHelper.pullGoods(pageIndex, pageSize, url, appKey, appSecret, sessionKey);
        ApiResultVo<TaoGoodsResponse> listApiResult = GoodsApiHelper.pullGoods(pageIndex, pageSize, url, appKey, appSecret, sessionKey,startTime,endTime);

        int insertSuccess = 0;//新增成功的订单
        int totalError = 0;
        int hasExistOrder = 0;//已存在的订单数

        for (var goods:listApiResult.getList()) {
            TaoGoods taoGoods = new TaoGoods();
            BeanUtils.copyProperties(goods,taoGoods);
            List<TaoGoodsSku> skuList = new ArrayList<>();
            if(goods.getSkus()!=null && goods.getSkus().size()>0) {
                for(var sku:goods.getSkus()) {
                    TaoGoodsSku taoGoodsSku = new TaoGoodsSku();
                    BeanUtils.copyProperties(sku,taoGoodsSku);
                    skuList.add(taoGoodsSku);
                }
            }
            taoGoods.setSkus(skuList);
            int result = goodsService.saveAndUpdateGoods(req.getShopId(), taoGoods);
            if (result == ResultVoEnum.DataExist.getIndex()) {
                //已经存在
                hasExistOrder++;
            } else if (result == ResultVoEnum.SUCCESS.getIndex()) {
                insertSuccess++;
            }else {
                totalError++;
            }
        }
        //计算总页数
        int totalPage = (listApiResult.getTotalRecords() % pageSize == 0) ? listApiResult.getTotalRecords() / pageSize.intValue() : (listApiResult.getTotalRecords() / pageSize.intValue()) + 1;
        pageIndex++;

        while (pageIndex <= totalPage) {

            ApiResultVo<TaoGoodsResponse> result1 = GoodsApiHelper.pullGoods(pageIndex, pageIndex, url, appKey, appSecret, sessionKey,startTime,endTime);
            //循环插入订单数据到数据库
            for (var goods:listApiResult.getList()) {
                TaoGoods taoGoods = new TaoGoods();
                BeanUtils.copyProperties(goods,taoGoods);
                List<TaoGoodsSku> skuList = new ArrayList<>();
                if(goods.getSkus()!=null && goods.getSkus().size()>0) {
                    for(var sku:goods.getSkus()) {
                        TaoGoodsSku taoGoodsSku = new TaoGoodsSku();
                        BeanUtils.copyProperties(sku,taoGoodsSku);
                        skuList.add(taoGoodsSku);
                    }
                }
                taoGoods.setSkus(skuList);
                int result = goodsService.saveAndUpdateGoods(req.getShopId(), taoGoods);
                if (result == ResultVoEnum.DataExist.getIndex()) {
                    //已经存在
                    hasExistOrder++;
                } else if (result == ResultVoEnum.SUCCESS.getIndex()) {
                    insertSuccess++;
                }else {
                    totalError++;
                }
            }
            pageIndex++;
        }
        OShopPullLogs logs = new OShopPullLogs();
        logs.setShopId(req.getShopId());
        logs.setShopType(EnumShopType.TAO.getIndex());
        logs.setPullType("GOODS");
        logs.setPullWay("主动拉取");
        logs.setPullParams("{PageNo:1,PageSize:100}");
        logs.setPullResult("{successTotal:"+listApiResult.getTotalRecords()+"}");
        logs.setPullTime(currDateTime);
        logs.setDuration(System.currentTimeMillis() - startTimeMillis);
        pullLogsService.save(logs);
        if(totalError == 0) {
            if (lasttime == null) {
                // 新增
                OShopPullLasttime insertLasttime = new OShopPullLasttime();
                insertLasttime.setShopId(req.getShopId());
                insertLasttime.setCreateTime(new Date());
                insertLasttime.setLasttime(endTime == null ? LocalDateTime.now() : endTime);
                insertLasttime.setPullType("GOODS");
                pullLasttimeService.save(insertLasttime);

            } else {
                // 修改
                OShopPullLasttime updateLasttime = new OShopPullLasttime();
                updateLasttime.setId(lasttime.getId());
                updateLasttime.setUpdateTime(new Date());
                updateLasttime.setLasttime(endTime == null ? LocalDateTime.now() : endTime);
                pullLasttimeService.updateById(updateLasttime);
            }
        }

        String msg = "成功，总共找到：" + listApiResult.getTotalRecords() + "条商品数据，新增：" + insertSuccess + "条，添加错误：" + totalError + "条，更新：" + hasExistOrder + "条";
        log.info(msg);
//        return new ApiResult<>(EnumResultVo.SUCCESS.getIndex(), msg);
        return AjaxResult.success(msg);
    }

}
