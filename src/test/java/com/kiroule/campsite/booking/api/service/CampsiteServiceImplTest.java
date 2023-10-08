package com.kiroule.campsite.booking.api.service;

import static com.kiroule.campsite.booking.api.TestHelper.CAMPSITE_ID;
import static com.kiroule.campsite.booking.api.TestHelper.buildCampsite;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.kiroule.campsite.booking.api.exception.CampsiteNotFoundException;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
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

  @InjectMocks CampsiteServiceImpl classUnderTest;

  Campsite existingCampsite;

  @BeforeEach
  void beforeEach() {
    existingCampsite = null;
  }

  @Nested
  @Disabled
  class FindById {

    Campsite foundCampsite;

    @BeforeEach
    void beforeEach() {
      foundCampsite = null;
    }

    @Test
    void happy_path() {
      given_existingCampsite();
      given_foundExistingCampsiteForId();

      when_findCampsiteById();

      then_assertCampsiteFound();
    }

    @Test
    void given_non_existing_campsite_uuid__then_campsite_not_found_and_exception_thrown() {
      given_foundNoExistingCampsiteForId();

      when_findCampsiteById_and_thenAssertExceptionThrown(CampsiteNotFoundException.class);
    }

    private void given_existingCampsite() {
      existingCampsite = buildCampsite();
    }

    private void given_foundNoExistingCampsiteForId() {
      doReturn(Optional.empty()).when(campsiteRepository).findById(any());
    }

    private void given_foundExistingCampsiteForId() {
      doReturn(Optional.of(existingCampsite)).when(campsiteRepository).findById(any());
    }

    private void when_findCampsiteById() {
      foundCampsite = classUnderTest.findById(CAMPSITE_ID);
    }

    private void then_assertCampsiteFound() {
      assertThat(foundCampsite).isEqualTo(existingCampsite);
      verify(campsiteRepository).findById(CAMPSITE_ID);
    }

    private void when_findCampsiteById_and_thenAssertExceptionThrown(
        Class<? extends Exception> exception) {
      assertThrows(exception, () -> classUnderTest.findById(any()));
    }
  }
}
