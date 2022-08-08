package com.kiroule.campsite.booking.api.config;

import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import com.kiroule.campsite.booking.api.service.BookingService;
import com.kiroule.campsite.booking.api.service.BookingServiceImpl;
import com.kiroule.campsite.booking.api.service.CampsiteService;
import com.kiroule.campsite.booking.api.service.CampsiteServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

  @Bean
  public BookingService bookingService(BookingRepository bookingRepository) {
    return new BookingServiceImpl(bookingRepository);
  }

  public CampsiteService campsiteService(CampsiteRepository campsiteRepository) {
    return new CampsiteServiceImpl(campsiteRepository);
  }

}
