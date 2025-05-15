package com.booktable.api.repository;

import com.booktable.api.model.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {
    
    List<Reservation> findByUserId(String userId);
    
    Page<Reservation> findByUserIdOrderByReservationDateTimeDesc(String userId, Pageable pageable);
    
    Page<Reservation> findByRestaurantIdOrderByReservationDateTimeDesc(String restaurantId, Pageable pageable);
    
    @Query("{'restaurantId': ?0, 'reservationDateTime': {$gte: ?1, $lte: ?2}}")
    List<Reservation> findByRestaurantIdAndDateRange(String restaurantId, LocalDateTime start, LocalDateTime end);
    
    @Query("{'status': ?0, 'reservationDateTime': {$gte: ?1}}")
    List<Reservation> findByStatusAndFutureDate(Reservation.ReservationStatus status, LocalDateTime now);
    
    @Query("{'restaurantId': ?0, 'tableId': ?1, 'reservationDateTime': {$gte: ?2, $lte: ?3}, 'status': {$ne: 'CANCELLED'}}")
    List<Reservation> findReservationsForTableInTimeRange(String restaurantId, String tableId, 
                                                         LocalDateTime start, LocalDateTime end);
    
    @Query(value = "{'reservationDateTime': {$gte: ?0, $lt: ?1}}", count = true)
    long countReservationsInDateRange(LocalDateTime start, LocalDateTime end);
}