package com.kiroule.campsite.booking.api;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.kiroule.campsite.booking.api.service.BookingServiceImpl;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("h2")
public class CampsiteBookingApiAppTestIT {

  @Autowired
  private ApplicationContext ctx;

  @Test
  public void contextLoads() {
    assertThat(ctx).isNotNull();
    AssertionsForClassTypes.assertThat(ctx.getBean(BookingServiceImpl.class)).isNotNull();
  }
}
