package com.qingfeng.framework.configure;

import com.qingfeng.auth.handler.MyAccessDeniedHandler;
import com.qingfeng.auth.handler.MyAuthExceptionEntryPoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ProjectName MyAuthExceptionConfigure
 * @author Administrator
 * @version 1.0.0
 * @Description TODO
 * @createTime 2021/4/3 0003 19:27
 */
@Configuration
public class MyAuthConfigure {

    @Bean
    @ConditionalOnMissingBean(name = "accessDeniedHandler")
    public MyAccessDeniedHandler accessDeniedHandler() {
        return new MyAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name = "authenticationEntryPoint")
    public MyAuthExceptionEntryPoint authenticationEntryPoint() {
        return new MyAuthExceptionEntryPoint();
    }
}