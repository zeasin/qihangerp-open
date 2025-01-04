package cn.qihangerp.app.openApi.jd.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.domain.OShopPlatform;
import cn.qihangerp.module.service.OShopPlatformService;
import cn.qihangerp.module.service.OShopService;
import cn.qihangerp.module.open.jd.domain.bo.JdTokenCreateBo;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@RequestMapping("/api/open-api/jd")
@AllArgsConstructor
@RestController
public class JdOAuthController {
    private final OShopPlatformService platformService;
    private final OShopService shopService;
    private static Logger log = LoggerFactory.getLogger(JdOAuthController.class);

    @GetMapping("/oauth")
    public AjaxResult OAuth(@RequestParam Integer shopId) throws IOException, InterruptedException {
        //查询店铺信息
//        var shop = shopService.selectShopById(shopId);
//        SysPlatform platform = platformService.selectById(shop.getType());
//        String url = "http://container.open.taobao.com/container?appkey=" + platform.getAppKey() + "&state=" + shopId;
        String url = "https://open-oauth.jd.com/oauth2/to_login?app_key=FB4CC3688E6F9065D4FF510A53BB60FF&response_type=code&redirect_uri=http%3A%2F%2Fwww.qumei.com&state="+shopId+"&scope=snsapi_base";
       return AjaxResult.success("SUCCESS",url);
    }

    @PostMapping("/tokenCreate")
    public AjaxResult callback(@RequestBody JdTokenCreateBo bo) throws IOException, InterruptedException {
        log.info("京东授权返回code");
        if (!StringUtils.hasText(bo.getCode())) return AjaxResult.error("code不能为空");
        OShopPlatform platform = platformService.getById(bo.getShopType());
//        String code = request.getParameter("code");//gwide6
//        JdClient client=new DefaultJdClient(serverUrl,accessToken,appKey,appSecret);

        //https://open-oauth.jd.com/oauth2/access_token?app_key=FB4CC3688E6F9065D4FF510A53BB60FF&app_secret=40e8c8b2427f4e6db8f4a39af27d719e&grant_type=authorization_code&code=gwide6

        String url = "https://open-oauth.jd.com/oauth2/access_token?app_key=" + platform.getAppKey() + "&app_secret=" + platform.getAppKey() + "&grant_type=authorization_code&code=" + bo.getCode();

        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)).header("Content-Type", "application/json").GET()
                .build();
        HttpResponse<String> result = client.send(request, HttpResponse.BodyHandlers.ofString());
        String s = result.body();
        if(s==""){
            //
        }
        /***
         *
         * {"access_token":"c9f997d0e44d4f118033c6e8e329f9e0zdbl","expires_in":31535999,"refresh_token":"c293b96a03a94013a471e50ebc3d2f9czi5m","scope":"snsapi_base","open_id":"PjmgY-ntsszAW81q9NRErnrUTVHhD-HC64HuspupG8Q","uid":"8474668550","time":1709518959147,"token_type":"bearer","code":0,"xid":"o*AASTnxHZ0sdNj9M3XhiMqYf0NTViY_IfFlQem3U6w1lv-neCalw"}
         *
         *
         * {
         * 	"access_token": "c9f997d0e44d4f118033c6e8e329f9e0zdbl",
         * 	"expires_in": 31535999,
         * 	"refresh_token": "c293b96a03a94013a471e50ebc3d2f9czi5m",
         * 	"scope": "snsapi_base",
         * 	"open_id": "PjmgY-ntsszAW81q9NRErnrUTVHhD-HC64HuspupG8Q",
         * 	"uid": "8474668550",
         * 	"time": 1709518959147,
         * 	"token_type": "bearer",
         * 	"code": 0,
         * 	"xid": "o*AASTnxHZ0sdNj9M3XhiMqYf0NTViY_IfFlQem3U6w1lv-neCalw"
         * }
         *
         *
         */


//        String sessionKey = request.getParameter("top_session");
//        String state = request.getParameter("state");
//        try {
//            Integer shopId = Integer.parseInt(state);
////            shopService.updateSessionKey(shopId, sessionKey);
//            return "redirect:/order/list?shopId=" + shopId;
//        } catch (Exception e) {
//            return "redirect:/?msg=callback_taobao_error";
//        }

        return AjaxResult.success("SUCCESS");
    }
}
