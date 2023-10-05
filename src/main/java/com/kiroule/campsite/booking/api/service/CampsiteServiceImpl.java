package com.kiroule.campsite.booking.api.service;

import com.kiroule.campsite.booking.api.exception.CampsiteNotFoundException;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link CampsiteService} interface implementation.
 *
 * @author Igor Baiborodine
 */
@Service
@AllArgsConstructor
public class CampsiteServiceImpl implements CampsiteService {

  private CampsiteRepository campsiteRepository;

  @Override
  @Transactional(readOnly = true)
  public Campsite findById(Long id) {

    return campsiteRepository
        .findById(id)
        .orElseThrow(
            () ->
                new CampsiteNotFoundException(
                    String.format("Booking was not found for id=%d", id)));
  }
}
