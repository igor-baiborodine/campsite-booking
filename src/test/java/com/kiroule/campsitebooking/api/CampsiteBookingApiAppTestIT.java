package com.kiroule.campsitebooking.api;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

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
    assertThat(ctx, is(notNullValue()));
    assertThat(ctx.getBean(BookingServiceImpl.class), is(notNullValue()));
  }
}
