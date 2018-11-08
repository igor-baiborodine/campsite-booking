package com.kiroule.campsitebooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableRetry
public class CampsiteBookingApplication {

  public static void main(String[] args) {
    SpringApplication.run(CampsiteBookingApplication.class, args);
  }
}
