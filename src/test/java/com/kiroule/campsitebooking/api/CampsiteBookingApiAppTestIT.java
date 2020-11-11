package com.kiroule.campsitebooking.api;

import com.kiroule.campsitebooking.api.service.BookingServiceImpl;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CampsiteBookingApiAppTestIT {

  @Autowired
  private ApplicationContext ctx;

  @Test
  public void contextLoads() {
    assertThat(ctx, is(notNullValue()));
    assertThat(ctx.getBean(BookingServiceImpl.class), is(notNullValue()));
  }
}
