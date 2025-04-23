package cn.qihangerp.api.tao;

//import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;


//@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class ,MybatisAutoConfiguration.class})
//@ComponentScan
//@Configuration
//@EnableAutoConfiguration
@Configuration
//@EnableAutoConfiguration(exclude = MybatisAutoConfiguration.class)
@ComponentScan(basePackages = "cn.qihangerp.api.tao",
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = TaoApiAutoConfiguration.class))
public class TaoApiAutoConfiguration {
}
