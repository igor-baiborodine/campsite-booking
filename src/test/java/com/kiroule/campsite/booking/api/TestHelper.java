package com.kiroule.campsite.booking.api;

import com.kiroule.campsite.booking.api.contract.v2.dto.BookingDto;
import com.kiroule.campsite.booking.api.model.Booking;
import com.kiroule.campsite.booking.api.model.Campsite;
import com.kiroule.campsite.booking.api.repository.entity.BookingEntity;
import com.kiroule.campsite.booking.api.repository.entity.CampsiteEntity;
import java.time.LocalDate;
import java.util.UUID;

public class TestHelper {

  public static final String FULL_NAME = "John Smith";
  public static final String EMAIL = "john.smith@domain.com";

  public static final Long CAMPSITE_ID = 1L;
  public static final int CAMPSITE_CAPACITY = 2;

  public static BookingDto buildBookingDto(LocalDate startDate, LocalDate endDate) {
    return buildBookingDto(startDate, endDate, UUID.randomUUID());
  }

  public static BookingDto buildBookingDto(LocalDate startDate, LocalDate endDate, UUID uuid) {
    return buildBookingDto(CAMPSITE_ID, startDate, endDate, uuid, FULL_NAME, EMAIL, true);
  }

  public static BookingDto buildBookingDto(
      Long campsiteId,
      LocalDate startDate,
      LocalDate endDate,
      UUID uuid,
      String fullName,
      String email,
      boolean active) {
    return BookingDto.builder()
        .campsiteId(campsiteId)
        .startDate(startDate)
        .endDate(endDate)
        .uuid(uuid)
        .fullName(fullName)
        .email(email)
        .active(active)
        .build();
  }

  public static Booking buildBooking(LocalDate startDate, LocalDate endDate) {
    return buildBooking(startDate, endDate, UUID.randomUUID());
  }

  public static Booking buildBooking(LocalDate startDate, LocalDate endDate, UUID uuid) {
    return buildBooking(buildCampsite(), startDate, endDate, uuid, FULL_NAME, EMAIL, true);
  }

  public static Booking buildBooking(
      LocalDate startDate, LocalDate endDate, UUID uuid, Campsite campsite) {
    return buildBooking(campsite, startDate, endDate, uuid, FULL_NAME, EMAIL, true);
  }

  public static Booking buildBooking(
      Campsite campsite,
      LocalDate startDate,
      LocalDate endDate,
      UUID uuid,
      String fullName,
      String email,
      boolean active) {
    return Booking.builder()
        .campsite(campsite)
        .startDate(startDate)
        .endDate(endDate)
        .uuid(uuid)
        .fullName(fullName)
        .email(email)
        .active(active)
        .build();
  }

  public static BookingEntity buildBookingEntity(LocalDate startDate, LocalDate endDate) {
    return buildBookingEntity(startDate, endDate, UUID.randomUUID());
  }

  public static BookingEntity buildBookingEntity(
      LocalDate startDate, LocalDate endDate, UUID uuid) {
    return buildBookingEntity(buildCampsiteEntity(), startDate, endDate, uuid, FULL_NAME, EMAIL, true);
  }

  public static BookingEntity buildBookingEntity(
      LocalDate startDate, LocalDate endDate, UUID uuid, CampsiteEntity campsite) {
    return buildBookingEntity(campsite, startDate, endDate, uuid, FULL_NAME, EMAIL, true);
  }

  public static BookingEntity buildBookingEntity(
      CampsiteEntity campsite,
      LocalDate startDate,
      LocalDate endDate,
      UUID uuid,
      String fullName,
      String email,
      boolean active) {
    return BookingEntity.builder()
        .campsite(campsite)
        .startDate(startDate)
        .endDate(endDate)
        .uuid(uuid)
        .fullName(fullName)
        .email(email)
        .active(active)
        .build();
  }

  public static Campsite buildCampsite() {
    return buildCampsite(CAMPSITE_ID, CAMPSITE_CAPACITY, true, true, true, true, true);
  }

  private static Campsite buildCampsite(
      Long id,
      int capacity,
      boolean restrooms,
      boolean drinkingWater,
      boolean picnicTable,
      boolean firePit,
      boolean active) {
    return Campsite.builder()
        .id(id)
        .capacity(capacity)
        .restrooms(restrooms)
        .drinkingWater(drinkingWater)
        .picnicTable(picnicTable)
        .firePit(firePit)
        .active(active)
        .build();
  }

  public static CampsiteEntity buildCampsiteEntity() {
    return buildCampsiteEntity(CAMPSITE_ID, CAMPSITE_CAPACITY, true, true, true, true, true);
  }

  private static CampsiteEntity buildCampsiteEntity(
      Long id,
      int capacity,
      boolean restrooms,
      boolean drinkingWater,
      boolean picnicTable,
      boolean firePit,
      boolean active) {
    return CampsiteEntity.builder()
        .id(id)
        .capacity(capacity)
        .restrooms(restrooms)
        .drinkingWater(drinkingWater)
        .picnicTable(picnicTable)
        .firePit(firePit)
        .active(active)
        .build();
  }
}
