package com.kiroule.campsite.booking.api.config;

import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import com.kiroule.campsite.booking.api.repository.context.CustomRepositoryContext;
import com.kiroule.campsite.booking.api.repository.context.DerbyCustomRepositoryContextImpl;
import com.kiroule.campsite.booking.api.repository.context.MysqlCustomRepositoryContextImpl;
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
  public CustomRepositoryContext inMemoryDbCustomRepositoryContext(EntityManager entityManager) {
    return new DerbyCustomRepositoryContextImpl(entityManager);
  }

  @Bean
  @Profile("mysql")
  public CustomRepositoryContext mySqlCustomRepositoryContext(EntityManager entityManager) {
    return new MysqlCustomRepositoryContextImpl(entityManager);
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
