package com.kiroule.campsitebooking.config;

import com.kiroule.campsitebooking.mapper.BookingMapper;
import com.kiroule.campsitebooking.mapper.CampsiteMapper;
import com.kiroule.campsitebooking.repository.BookingRepository;
import com.kiroule.campsitebooking.repository.CampsiteRepository;
import com.kiroule.campsitebooking.repository.context.CustomRepositoryContext;
import com.kiroule.campsitebooking.repository.context.DerbyCustomRepositoryContextImpl;
import com.kiroule.campsitebooking.repository.context.MysqlCustomRepositoryContextImpl;
import com.kiroule.campsitebooking.service.BookingService;
import com.kiroule.campsitebooking.service.BookingServiceImpl;
import com.kiroule.campsitebooking.service.CampsiteService;
import com.kiroule.campsitebooking.service.CampsiteServiceImpl;
import jakarta.persistence.EntityManager;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = {QueryProperties.class})
public class AppConfig {

  @Bean
  @Profile("in-memory-db")
  public CustomRepositoryContext inMemoryDbCustomRepositoryContext(EntityManager entityManager) {
    return new DerbyCustomRepositoryContextImpl(entityManager);
  }

  @Bean
  @Profile("mysql")
  public CustomRepositoryContext mySqlCustomRepositoryContext(EntityManager entityManager) {
    return new MysqlCustomRepositoryContextImpl(entityManager);
  }

  @Bean
  public BookingService bookingService(
      BookingRepository bookingRepository, BookingMapper bookingMapper) {
    return new BookingServiceImpl(bookingRepository, bookingMapper);
  }

  @Bean
  public CampsiteService campsiteService(
      CampsiteRepository campsiteRepository, CampsiteMapper campsiteMapper) {
    return new CampsiteServiceImpl(campsiteRepository, campsiteMapper);
  }
}
