package com.kiroule.campsite.booking.api.service;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.assertj.core.api.Assertions.assertThat;

import com.kiroule.campsite.booking.api.BaseIT;
import com.kiroule.campsite.booking.api.TestDataHelper;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
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
class CampsiteServiceImplIT extends BaseIT {

  @Autowired
  @Qualifier("campsiteService")
  CampsiteService classUnderTest;

  @Autowired TestDataHelper testDataHelper;

  Campsite existingCampsite;

  @BeforeEach
  void beforeEach() {
    existingCampsite = null;
  }

  @Nested
  class FindById {

    @Test
    void happy_path() {
      // given
      CampsiteEntity campsiteEntity = testDataHelper.createCampsiteEntity();
      // when
      Campsite result = classUnderTest.findById(campsiteEntity.getId());
      // then
      assertThat(result)
          .usingRecursiveComparison()
          .ignoringFields("createdAt", "updatedAt")
          .isEqualTo(campsiteEntity);
      assertThat(result.getCreatedAt().truncatedTo(MILLIS))
          .isEqualTo(campsiteEntity.getCreatedAt().truncatedTo(MILLIS));
      assertThat(result.getUpdatedAt().truncatedTo(MILLIS))
          .isEqualTo(campsiteEntity.getUpdatedAt().truncatedTo(MILLIS));
    }
  }
}
