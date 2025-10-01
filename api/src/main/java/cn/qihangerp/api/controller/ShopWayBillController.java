package cn.qihangerp.api.controller;

import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.security.common.BaseController;
import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@RequestMapping("/shop/ewaybill")
@RestController
@AllArgsConstructor
public class ShopWayBillController extends BaseController {

    /**
     * 获取电子面单账户
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/get_waybill_account_list", method = RequestMethod.GET)
    public AjaxResult getWaybillAccountList() throws Exception {
        return AjaxResult.error("开源版本不支持电子面单相关功能");
    }

    /**
     * 拉取电子面单账户
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/pull_waybill_account", method = RequestMethod.POST)
    public AjaxResult pull_waybill_account() throws Exception {
        return AjaxResult.error("开源版本不支持电子面单相关功能");
    }

    @RequestMapping(value = "/updateAccount", method = RequestMethod.POST)
    public AjaxResult updateAccount(  ) throws Exception {
        return AjaxResult.error("开源版本不支持电子面单相关功能");
    }

}
