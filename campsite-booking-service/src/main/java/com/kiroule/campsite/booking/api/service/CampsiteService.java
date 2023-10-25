package com.kiroule.campsite.booking.api.service;

import com.kiroule.campsite.booking.api.model.Campsite;

public interface CampsiteService {

  Campsite findById(Long id);
}
