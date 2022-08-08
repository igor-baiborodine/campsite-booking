package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.CAMPSITE_ID;
import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsite.booking.api.CustomReplaceUnderscoresDisplayNameGenerator;
import com.kiroule.campsite.booking.api.model.Campsite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration tests for {@link CampsiteServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@SpringBootTest
@ActiveProfiles("h2")
@DisplayNameGeneration(CustomReplaceUnderscoresDisplayNameGenerator.class)
class CampsiteServiceImplTestIT {

  @Autowired
  CampsiteService campsiteService;

  Campsite existingCampsite;

  @BeforeEach
  void beforeEach() {
    existingCampsite = null;
  }

  @Nested
  class Find_By_Id {


    @Test
    void happy_path() {
      // given existing campsite with id=1

      when_findById();

      then_assertCampsiteFound();
    }

    private void when_findById() {
      existingCampsite = campsiteService.findById(CAMPSITE_ID);
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
