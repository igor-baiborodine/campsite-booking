package com.kiroule.campsite.booking.api.config;

import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import com.kiroule.campsite.booking.api.repository.context.CustomizedRepositoryContext;
import com.kiroule.campsite.booking.api.repository.context.DerbyCustomizedRepositoryContextImpl;
import com.kiroule.campsite.booking.api.repository.context.MySqlCustomizedRepositoryContextImpl;
import com.kiroule.campsite.booking.api.repository.context.PostgreSqlCustomizedRepositoryContextImpl;
import com.kiroule.campsite.booking.api.service.BookingService;
import com.kiroule.campsite.booking.api.service.BookingServiceImpl;
import com.kiroule.campsite.booking.api.service.CampsiteService;
import com.kiroule.campsite.booking.api.service.CampsiteServiceImpl;
import javax.persistence.EntityManager;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConfigurationPropertiesScan(basePackageClasses = {QueryProperties.class})
public class CampsiteBookingApiConfig {

  @Bean
  @Profile("in-memory-db")
  public CustomizedRepositoryContext inMemoryDbCustomizedRepositoryContext(EntityManager entityManager) {
    return new DerbyCustomizedRepositoryContextImpl(entityManager);
  }

  @Bean
  @Profile("mysql")
  public CustomizedRepositoryContext mySqlCustomizedRepositoryContext(EntityManager entityManager) {
    return new MySqlCustomizedRepositoryContextImpl(entityManager);
  }

  @Bean
  @Profile("postgresql")
  public CustomizedRepositoryContext postgreSqlCustomizedRepositoryContext(EntityManager entityManager) {
    return new PostgreSqlCustomizedRepositoryContextImpl(entityManager);
  }

  @Bean
  public BookingService bookingService(BookingRepository bookingRepository) {
    return new BookingServiceImpl(bookingRepository);
  }

  @Bean
  public CampsiteService campsiteService(CampsiteRepository campsiteRepository) {
    return new CampsiteServiceImpl(campsiteRepository);
  }

}
