package com.booktable.api.dto;

import com.booktable.api.model.Reservation.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    
    private String id;
    private String restaurantId;
    private String restaurantName;
    private String userId;
    private String userFullName;
    private String tableId;
    private LocalDateTime reservationDateTime;
    private int partySize;
    private String specialRequests;
    private ReservationStatus status;
    private Date createdAt;
    private Date updatedAt;
}