package com.kiroule.campsitebooking.repository;

import com.kiroule.campsitebooking.repository.entity.CampsiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CampsiteRepository extends JpaRepository<CampsiteEntity, Long> {}
