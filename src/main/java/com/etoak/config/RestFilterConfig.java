package com.etoak.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

/**
 * 支持同步请求发送rest请求的过滤器
 */
@Configuration
public class RestFilterConfig {

    /**
     * 注册HiddenHttpMethodFilter
     * 作用: 将post转成put、delete请求
     * 要求:
     *  1. 表单提交方式必须post
     *  2. 表单必须有一个隐藏域, name属性值是_method
     * @return
     */
    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> resFilter(){
        FilterRegistrationBean<HiddenHttpMethodFilter> restFilter =
                new FilterRegistrationBean<>(new HiddenHttpMethodFilter());
        restFilter.addUrlPatterns("/*");
        return restFilter;
    }
}
