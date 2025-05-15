package com.booktable.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationCreateRequest {
    
    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;
    
    @NotNull(message = "Reservation date and time is required")
    private LocalDateTime reservationDateTime;
    
    @NotNull(message = "Party size is required")
    @Min(value = 1, message = "Party size must be at least 1")
    private Integer partySize;
    
    private String specialRequests;
}