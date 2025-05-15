package com.booktable.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    
    private String id;
    private String restaurantId;
    private String restaurantName;
    private String userId;
    private String userFullName;
    private String reservationId;
    private int rating;
    private String comment;
    private Date createdAt;
    private Date updatedAt;
}