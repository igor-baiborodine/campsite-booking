package com.kiroule.campsitebooking.api;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import com.kiroule.campsitebooking.api.service.BookingServiceImpl;
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
    assertThat(ctx.getBean(BookingServiceImpl.class)).isNotNull();
  }
}
