package com.kiroule.campsitebooking.service;

import com.kiroule.campsitebooking.model.Campsite;

public interface CampsiteService {

  Campsite findById(Long id);
}
