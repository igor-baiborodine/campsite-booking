package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.CAMPSITE_ID;
import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsite.booking.api.BaseTestIT;
import com.kiroule.campsite.booking.api.model.Campsite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Integration tests for {@link CampsiteServiceImpl}.
 *
 * @author Igor Baiborodine
 */
class CampsiteServiceImplTestIT extends BaseTestIT {

  @Autowired @Qualifier("campsiteService")
  CampsiteService classUnderTest;

  Campsite existingCampsite;

  @BeforeEach
  void beforeEach() {
    existingCampsite = null;
  }

  @Nested
  class FindById {

    @Test
    void happy_path() {
      // given existing campsite with id=1

      when_findById();

      then_assertCampsiteFound();
    }

    private void when_findById() {
      existingCampsite = classUnderTest.findById(CAMPSITE_ID);
    }

    private void then_assertCampsiteFound() {
      assertThat(existingCampsite.getId()).isEqualTo(CAMPSITE_ID);
      assertThat(existingCampsite.getCapacity()).isEqualTo(2);
      assertThat(existingCampsite.isRestrooms()).isTrue();
      assertThat(existingCampsite.isDrinkingWater()).isTrue();
      assertThat(existingCampsite.isPicnicTable()).isTrue();
      assertThat(existingCampsite.isFirePit()).isTrue();
      assertThat(existingCampsite.isActive()).isTrue();
    }
  }
}
