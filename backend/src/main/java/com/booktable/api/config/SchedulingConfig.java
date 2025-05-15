package com.booktable.api.config;

import com.booktable.api.model.Reservation;
import com.booktable.api.model.Restaurant;
import com.booktable.api.repository.ReservationRepository;
import com.booktable.api.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SchedulingConfig {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;

    // Reset daily booking counts at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyBookingCounts() {
        log.info("Resetting daily booking counts for all restaurants");
        List<Restaurant> restaurants = restaurantRepository.findAll();
        for (Restaurant restaurant : restaurants) {
            restaurant.setTotalBookingsToday(0);
        }
        restaurantRepository.saveAll(restaurants);
    }

    // Mark reservations as completed if they are past the reservation time
    @Scheduled(cron = "0 0 * * * ?") // Run every hour
    public void updateReservationStatuses() {
        log.info("Updating reservation statuses");
        LocalDateTime now = LocalDateTime.now();
        
        // Find confirmed reservations that are in the past
        List<Reservation> pastReservations = reservationRepository.findByStatusAndFutureDate(
                Reservation.ReservationStatus.CONFIRMED, now.minusHours(3));
        
        for (Reservation reservation : pastReservations) {
            // If reservation time is more than 3 hours ago, mark as completed
            if (reservation.getReservationDateTime().plusHours(3).isBefore(now)) {
                reservation.setStatus(Reservation.ReservationStatus.COMPLETED);
                log.info("Marking reservation {} as COMPLETED", reservation.getId());
            }
        }
        
        if (!pastReservations.isEmpty()) {
            reservationRepository.saveAll(pastReservations);
        }
    }
}