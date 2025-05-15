package com.booktable.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
@CompoundIndex(name = "restaurant_user_index", def = "{'restaurantId': 1, 'userId': 1}")
public class Review {
    
    @Id
    private String id;
    
    private String restaurantId;
    
    private String userId;
    
    private String reservationId;
    
    private int rating; // 1-5 stars
    
    private String comment;
    
    @Builder.Default
    private Date createdAt = new Date();
    
    private Date updatedAt;
}