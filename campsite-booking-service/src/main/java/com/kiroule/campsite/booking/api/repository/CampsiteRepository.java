package com.kiroule.campsite.booking.api.repository;

import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampsiteRepository extends JpaRepository<CampsiteEntity, Long> {}
