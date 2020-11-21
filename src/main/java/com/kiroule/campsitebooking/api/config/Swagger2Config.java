package com.kiroule.campsitebooking.api.config;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.client.LinkDiscoverer;
import org.springframework.hateoas.client.LinkDiscoverers;
import org.springframework.hateoas.mediatype.collectionjson.CollectionJsonLinkDiscoverer;
import org.springframework.plugin.core.SimplePluginRegistry;
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
@ComponentScan(basePackages = "com.kiroule.campsitebooking.api.controller")
public class Swagger2Config {

  @Bean
  public LinkDiscoverers discoverers() {
    List<LinkDiscoverer> plugins = new ArrayList<>();
    plugins.add(new CollectionJsonLinkDiscoverer());
    return new LinkDiscoverers(SimplePluginRegistry.create(plugins));

  }

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
            "igor at kiroule dot com"),
        "Apache 2.0",
        "http://www.apache.org/licenses/LICENSE-2.0",
        Lists.newArrayList());
  }

}
