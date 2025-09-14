package cn.qihangerp.api.controller;


import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class HomeController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Operation(summary = "获取用户信息", description = "根据用户ID获取详细的用户信息")
    @GetMapping("/")
    public String home(){
        return "{'code':0,'msg':'请通过api访问'}";
    }
    @GetMapping(value = "/echo-rest")
    public String rest() {
        return restTemplate.getForObject("http://tao-oms/test/na", String.class);
    }

}
