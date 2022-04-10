package com.qingfeng.framework.servlet;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author Administrator
 * @version 1.0.0
 * @ProjectName qingfeng
 * @Description 注册拦截器
 * @createTime 2022年01月12日 13:29:00
 */
@Configuration
public class WebConfigurer  implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
        //多个拦截器时 以此添加 执行顺序按添加顺序
        registry.addInterceptor(getHandlerInterceptor())
                .addPathPatterns("/common/**").excludePathPatterns("/login/**");
    }

    // 这个方法是用来配置静态资源的，比如html，js，css，等等
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
    }

    //后台
    @Bean
    public static HandlerInterceptor getHandlerInterceptor() {
        return new CustomInterceptor();
    }

}