package cn.qihangerp.app.openApi.tao.controller;

import cn.qihangerp.app.security.common.BaseController;
import cn.qihangerp.common.AjaxResult;
import cn.qihangerp.module.open.tao.domain.bo.TaoOrderShipBo;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.web.bind.annotation.*;

@Log
@AllArgsConstructor
@RestController
@RequestMapping("/api/open-api/tao/ship")
public class TaoShipController extends BaseController {

    @PostMapping("/order_ship")
    @ResponseBody
    public AjaxResult orderShip(@RequestBody TaoOrderShipBo bo) {
        log.info("接收到oms发货通知：开始推送到tao平台");
        String tid= bo.getTid();
        return success();
    }
}
