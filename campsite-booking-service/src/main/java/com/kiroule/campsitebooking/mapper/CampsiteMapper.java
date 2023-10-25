package com.kiroule.campsitebooking.mapper;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

import com.kiroule.campsitebooking.model.Campsite;
import com.kiroule.campsitebooking.repository.entity.CampsiteEntity;
import org.mapstruct.Mapper;

@Mapper(
    componentModel = SPRING,
    injectionStrategy = CONSTRUCTOR)
public interface CampsiteMapper {

  Campsite toCampsite(CampsiteEntity campsiteEntity);
}
