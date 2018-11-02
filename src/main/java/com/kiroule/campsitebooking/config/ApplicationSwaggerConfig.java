package com.kiroule.campsitebooking.config;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Java config for Springfox Swagger 2 documentation plugin.
 *
 * @author Igor Baiborodine
 */
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = "com.kiroule.campsitebooking.api.rest")
public class ApplicationSwaggerConfig {

  @Bean
  public Docket customDocket() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any())
        .build()
        .apiInfo(getApiInfo());
  }

  private ApiInfo getApiInfo() {
    return new ApiInfo(
        "Campsite Booking REST API Documentation",
        "This is REST API documentation of the Campsite Booking REST web service.",
        "1.0",
        "Campsite Booking REST API terms of service",
        new Contact(
            "Igor Baiborodine",
            "https://github.com/igor-baiborodine/campsite-booking",
            "igor dot baiborodine at yandex dot com"),
        "Apache 2.0",
        "http://www.apache.org/licenses/LICENSE-2.0",
        Lists.newArrayList());
  }

}
