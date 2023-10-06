package com.kiroule.campsite.booking.api.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = SPRING,
    injectionStrategy = CONSTRUCTOR)
public interface CampsiteMapper {

  Campsite toCampsite(CampsiteEntity campsiteEntity);
}
