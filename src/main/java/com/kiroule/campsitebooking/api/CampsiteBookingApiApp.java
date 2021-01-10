package com.kiroule.campsitebooking.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
@EnableRetry
public class CampsiteBookingApiApp {

  public static void main(String[] args) {
    SpringApplication.run(CampsiteBookingApiApp.class, args);
  }
}
