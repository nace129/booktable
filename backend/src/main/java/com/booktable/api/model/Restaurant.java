package com.booktable.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "restaurants-entries")
@CompoundIndex(name = "location_index", def = "{'address.city': 1, 'address.state': 1, 'address.zipCode': 1}")
public class Restaurant {
    
    @Id
    private String id;
    
    @Indexed
    private String name;
    
    private String description;
    
    private Address address;
    
    private String phoneNumber;
    
    private String email;
    
    private String website;
    
    private List<String> cuisineTypes;
    
    private Integer priceRange; // 1-4 indicating $ to $$$$
    
    @Builder.Default
    private List<OpeningHours> openingHours = new ArrayList<>();
    
    @Builder.Default
    private List<Table> tables = new ArrayList<>();
    
    @Builder.Default
    private List<String> images = new ArrayList<>();
    
    private String managerId;
    
    @Builder.Default
    private Date createdAt = new Date();
    
    @Builder.Default
    private boolean approved = false;
    
    @Builder.Default
    private boolean active = true;
    
    // Statistics and metrics
    @Builder.Default
    private double averageRating = 0.0;
    
    @Builder.Default
    private int totalReviews = 0;
    
    @Builder.Default
    private int totalBookingsToday = 0;
}