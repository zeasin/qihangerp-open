package cn.qihangerp.app.openApi.pdd.controller;


import cn.qihangerp.app.openApi.pdd.TokenCreateBo;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.common.enums.EnumShopType;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.service.OShopPlatformService;
import cn.qihangerp.module.service.OShopService;

import com.pdd.pop.sdk.http.PopAccessTokenClient;
import com.pdd.pop.sdk.http.token.AccessTokenResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;

@AllArgsConstructor
@RequestMapping("/api/open-api/pdd")
@RestController
public class PddOAuthController {
    private final OShopService shopService;
    private final OShopPlatformService platformService;
//    @Autowired
//    private IShopService shopService;
//    @Autowired
//    private ServerConfig serverConfig;
    private static Logger log = LoggerFactory.getLogger(PddOAuthController.class);

    @GetMapping("/getOauthUrl")
    public AjaxResult oauth(@RequestParam Integer shopId) {
//        String returnUrl = serverConfig.getUrl() + "/pdd_api/getToken&state="+req.getParameter("shopId");
//        var shop = shopService.selectShopById(reqData.getShopId());
        OShopPlatform platform = platformService.selectById(EnumShopType.PDD.getIndex());
        String appKey = platform.getAppKey();
        String appSercet = platform.getAppSecret();

        String url = "https://mms.pinduoduo.com/open.html?response_type=code&client_id=" + appKey + "&redirect_uri=" + URLEncoder.encode(platform.getRedirectUri());
        return AjaxResult.success("SUCCESS",url);
    }

    @PostMapping("/getToken")
    public AjaxResult getToken(@RequestBody TokenCreateBo bo) throws IOException, InterruptedException {
        log.info("/**********获取拼多多授权token*********/");
        var shop = shopService.selectShopById(bo.getShopId());
        OShopPlatform platform = platformService.selectById(EnumShopType.PDD.getIndex());
        String appKey = platform.getAppKey();
        String appSercet = platform.getAppSecret();

        PopAccessTokenClient accessTokenClient = new PopAccessTokenClient(appKey, appSercet);

        // 生成AccessToken
        try {

            AccessTokenResponse response = accessTokenClient.generate(bo.getCode());
            if(response.getErrorResponse()!=null){
                log.info("/***************获取拼多多授权token错误："+response.getErrorResponse().getErrorMsg()+"**************/");
            }else{
                //保存accessToken
                shopService.updateSessionKey(bo.getShopId(),response.getAccessToken());

            }
        } catch (Exception e) {

            e.printStackTrace();

        }
        return AjaxResult.success("SUCCESS");
    }

//    /**
//     * 获取授权成功
//     * @param req
//     * @param model
//     * @return
//     */
//    @RequestMapping("/getTokenSuccess")
//    public String getTokeSuccess(HttpServletRequest req, @RequestParam Long mallId, Model model){
//        var shop = shopService.selectShopById(mallId);
//        model.addAttribute("shop",shop);
//        model.addAttribute("shopId",shop.getId());
//        return "get_token_success";
//    }



}
