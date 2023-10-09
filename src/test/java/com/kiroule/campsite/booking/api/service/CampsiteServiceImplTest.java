package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestDataHelper.nextCampsite;
import static com.kiroule.campsite.booking.api.TestDataHelper.nextCampsiteEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.kiroule.campsite.booking.api.exception.CampsiteNotFoundException;
import com.kiroule.campsite.booking.api.mapper.CampsiteMapper;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit tests for {@link CampsiteServiceImpl}.
 *
 * @author Igor Baiborodine
 */
@ExtendWith(MockitoExtension.class)
class CampsiteServiceImplTest {

  @Mock CampsiteRepository campsiteRepository;

  @Mock CampsiteMapper campsiteMapper;

  @InjectMocks CampsiteServiceImpl classUnderTest;

  @Nested
  class FindById {

    @Test
    void happy_path() {
      // given
      Campsite campsite = nextCampsite();
      CampsiteEntity campsiteEntity = nextCampsiteEntity();
      doReturn(Optional.of(campsiteEntity)).when(campsiteRepository).findById(any());
      doReturn(campsite).when(campsiteMapper).toCampsite(any(CampsiteEntity.class));
      // when
      Campsite result = classUnderTest.findById(campsite.getId());
      // then
      assertThat(result).isEqualTo(campsite);
      verify(campsiteRepository).findById(campsite.getId());
      verify(campsiteMapper).toCampsite(campsiteEntity);
    }

    @Test
    void given_non_existing_campsite_uuid__then_campsite_not_found_and_exception_thrown() {
      // given
      Campsite campsite = nextCampsite();
      // when
      Executable executable = () -> classUnderTest.findById(campsite.getId());
      // then
      assertThrows(CampsiteNotFoundException.class, executable);
      verify(campsiteRepository).findById(campsite.getId());
      verify(campsiteMapper, never()).toCampsite(any());
    }
  }
}
