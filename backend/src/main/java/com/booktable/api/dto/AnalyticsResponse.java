package com.booktable.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsResponse {
    
    private long totalReservations;
    private long confirmedReservations;
    private long cancelledReservations;
    private long completedReservations;
    private long noShowReservations;
    
    // Reservations per day for the last 30 days
    @Builder.Default
    private Map<String, Long> reservationsPerDay = new HashMap<>();
    
    // Reservations by restaurant (top 10)
    @Builder.Default
    private Map<String, Long> topRestaurants = new HashMap<>();
    
    // Reservations by cuisine type
    @Builder.Default
    private Map<String, Long> reservationsByCuisine = new HashMap<>();
}