package com.kiroule.campsite.booking.api.repository;

import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
import org.springframework.data.repository.CrudRepository;

public interface CampsiteRepository extends CrudRepository<CampsiteEntity, Long> {}
