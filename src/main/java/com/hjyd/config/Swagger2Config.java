package com.hjyd.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @program：ServiceA
 * @description：
 * @author：黄细初
 * @create：2019-05-05 10:50
 */

@EnableSwagger2
@Configuration
public class Swagger2Config {

    public static final String ADMIN_VERSION = "1.0.0";


    @Bean
    public Docket createRestAPI(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("接口文档")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.hjyd.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("接口文档 APIs")
                .description("------------------------------------")
                .termsOfServiceUrl("http://192.168.10.11:7001")
                .contact(new Contact("华建云鼎科技","",""))
                .version(ADMIN_VERSION)
                .build();
    }

}
