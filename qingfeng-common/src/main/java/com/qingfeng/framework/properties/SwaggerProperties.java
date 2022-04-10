package com.qingfeng.framework.properties;

import lombok.Data;

/**
 * @ProjectName SwaggerProperties
 * @author Administrator
 * @version 1.0.0
 * @Description Swagger参数配置
 * @createTime 2022/4/8 0008 0:17
 */
@Data
public class SwaggerProperties {

    private String basePackage;
    private String title;
    private String description;
    private String version;
    private String author;
    private String url;
    private String email;
    private String license;
    private String licenseUrl;

    private String grantUrl;
    private String name;
    private String scope;

}