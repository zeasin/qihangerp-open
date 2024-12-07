package cn.qihangerp.app.openApi.jd.controller;

import cn.qihangerp.app.openApi.jd.JdApiCommon;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.api.ShopApiParams;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.common.enums.HttpStatus;
import cn.qihangerp.domain.OLogisticsCompany;
import cn.qihangerp.module.service.OLogisticsCompanyService;
import cn.qihangerp.module.service.OShopPlatformService;
import cn.qihangerp.module.service.OShopService;

import cn.qihangerp.sdk.jd.PullRequest;
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.request.delivery.GetVenderAllDeliveryCompanyRequest;
import com.jd.open.api.sdk.response.delivery.GetVenderAllDeliveryCompanyResponse;
import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Log
@RequestMapping("/api/open-api/jd/shopApi")
@RestController
@AllArgsConstructor
public class JdLogisticsApiController {
    private final JdApiCommon jdApiCommon;
    private final OShopPlatformService platformService;
    private final OShopService shopService;
    private final OLogisticsCompanyService logisticsCompanyService;

    @RequestMapping(value = "/pull_logistics_companies", method = RequestMethod.POST)
    public AjaxResult pullLogisticsList(@RequestBody PullRequest params) throws Exception {
        if (params.getShopId() == null || params.getShopId() <= 0) {
            return AjaxResult.error(HttpStatus.PARAMS_ERROR, "参数错误，没有店铺Id");
        }
        var checkResult = jdApiCommon.checkBefore(params.getShopId());
        if (checkResult.getCode() != HttpStatus.SUCCESS) {
            return AjaxResult.error(checkResult.getCode(), checkResult.getMsg());
        }

        ShopApiParams shopApiParams = checkResult.getData();
        String accessToken = shopApiParams.getAccessToken();
        String url = shopApiParams.getServerUrl();
        String appKey = shopApiParams.getAppKey();
        String appSecret = shopApiParams.getAppSecret();
        JdClient client=new DefaultJdClient(url,accessToken,appKey,appSecret);
        GetVenderAllDeliveryCompanyRequest request=new GetVenderAllDeliveryCompanyRequest();
        request.setFields("id,name,description");
        GetVenderAllDeliveryCompanyResponse response=client.execute(request);

        if(response.getDeliveryList() !=null) {
            for (var item : response.getDeliveryList()) {
                OLogisticsCompany lc = new OLogisticsCompany();
                lc.setPlatformId(EnumShopType.JD.getIndex());
                lc.setShopId(params.getShopId());
                lc.setCode(item.getId() + "");
                lc.setName(item.getName());
                lc.setLogisticsId(item.getId());
                lc.setRemark(item.getDescription());
                logisticsCompanyService.insert(lc);
                log.info("=====添加快递公司JD=====");
            }

        }else{
            AjaxResult.error(response.getMsg());
        }
//        System.out.println(rsp.getBody());
        return AjaxResult.success();
    }
}
