package com.booktable.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restaurants-entries")
@CompoundIndex(name = "restaurant_date_index", def = "{'restaurantId': 1, 'reservationDateTime': 1}")
public class Reservation {
    
    @Id
    private String id;
    
    private String restaurantId;
    
    private String userId;
    
    private String tableId;
    
    private LocalDateTime reservationDateTime;
    
    private int partySize;
    
    private String specialRequests;
    
    private ReservationStatus status;
    
    @Builder.Default
    private Date createdAt = new Date();
    
    private Date updatedAt;
    
    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        CANCELLED,
        COMPLETED,
        NO_SHOW
    }
}