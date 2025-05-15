package com.booktable.api.dto;

import com.booktable.api.model.Reservation.ReservationStatus;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationUpdateRequest {
    
    private LocalDateTime reservationDateTime;
    
    @Min(value = 1, message = "Party size must be at least 1")
    private Integer partySize;
    
    private String specialRequests;
    
    private ReservationStatus status;
}