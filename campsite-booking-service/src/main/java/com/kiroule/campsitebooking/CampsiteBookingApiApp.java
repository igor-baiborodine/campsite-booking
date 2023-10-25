package com.kiroule.campsitebooking;

import com.kiroule.campsitebooking.repository.CampsiteRepository;
import com.kiroule.campsitebooking.repository.entity.CampsiteEntity;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication
@EnableAspectJAutoProxy
@EnableJpaAuditing
@EnableRetry
@Generated
@AllArgsConstructor
public class CampsiteBookingApiApp implements ApplicationRunner {

  private final CampsiteRepository campsiteRepository;

  public static void main(String[] args) {
    SpringApplication.run(CampsiteBookingApiApp.class, args);
  }

  @Override
  public void run(ApplicationArguments args) {

    for (int i = 1; i < 4; i++) {
      CampsiteEntity campsite =
          CampsiteEntity.builder()
              .id((long) i)
              .capacity(i * 2)
              .restrooms(true)
              .drinkingWater(true)
              .picnicTable(true)
              .firePit(true)
              .active(true)
              .build();

      campsiteRepository.save(campsite);
    }
  }
}
