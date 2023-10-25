package com.kiroule.campsite.booking.api.service;

import com.kiroule.campsite.booking.api.exception.CampsiteNotFoundException;
import com.kiroule.campsite.booking.api.mapper.CampsiteMapper;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.CampsiteRepository;
import java.util.function.Supplier;
import lombok.AllArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

/**
 * {@link CampsiteService} interface implementation.
 *
 * @author Igor Baiborodine
 */
@AllArgsConstructor
public class CampsiteServiceImpl implements CampsiteService {

  private CampsiteRepository campsiteRepository;

  private CampsiteMapper campsiteMapper;

  @Override
  @Transactional(readOnly = true)
  public Campsite findById(Long id) {

    Supplier<CampsiteNotFoundException> exceptionSupplier =
        () -> new CampsiteNotFoundException(String.format("Booking was not found for id=%d", id));
    var campsiteEntity = campsiteRepository.findById(id).orElseThrow(exceptionSupplier);
    return campsiteMapper.toCampsite(campsiteEntity);
  }
}
