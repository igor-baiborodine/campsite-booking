package com.kiroule.campsite.booking.api.repository;

import com.kiroule.campsite.booking.api.model.Campsite;
import org.springframework.data.repository.CrudRepository;

public interface CampsiteRepository extends CrudRepository<Campsite, Long> {

}
