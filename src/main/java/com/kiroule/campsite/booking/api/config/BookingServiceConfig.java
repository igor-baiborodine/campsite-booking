package com.kiroule.campsite.booking.api.config;

import com.kiroule.campsite.booking.api.repository.BookingRepository;
import com.kiroule.campsite.booking.api.service.BookingService;
import com.kiroule.campsite.booking.api.service.BookingServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingServiceConfig {

  @Bean
  public BookingService bookingService(BookingRepository bookingRepository) {
    return new BookingServiceImpl(bookingRepository);
  }

}
