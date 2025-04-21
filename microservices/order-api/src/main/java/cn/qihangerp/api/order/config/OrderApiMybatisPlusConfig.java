package cn.qihangerp.api.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"cn.qihangerp.module.*.mapper"})
public class OrderApiMybatisPlusConfig {

//    @Bean(name = "mybatisPlusInterceptorGoodsApi")
//    public MybatisPlusInterceptor mybatisPlusInterceptor() {
//        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
//        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL)); //注意使用哪种数据库
//        return interceptor;
//    }
}
