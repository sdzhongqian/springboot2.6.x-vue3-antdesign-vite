package com.qingfeng;

import com.qingfeng.base.annotation.EnableMyProtect;
import com.qingfeng.base.annotation.EnableMyRedis;
import io.micrometer.core.instrument.MeterRegistry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@MapperScan("com.qingfeng.*.mapper")
@EnableMyRedis
@EnableMyProtect
@ServletComponentScan("com.qingfeng.framework.servlet")
public class QingfengApplication {

    @Value("${spring.application.name}")
    private  String application;

    public static void main(String[] args) {
        SpringApplication.run(QingfengApplication.class, args);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer() {
        return (registry) -> registry.config().commonTags("application", application);
    }

}
