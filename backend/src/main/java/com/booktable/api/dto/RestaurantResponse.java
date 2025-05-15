package com.booktable.api.dto;

import com.booktable.api.model.Address;
import com.booktable.api.model.OpeningHours;
import com.booktable.api.model.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantResponse {
    
    private String id;
    private String name;
    private String description;
    private Address address;
    private String phoneNumber;
    private String email;
    private String website;
    private List<String> cuisineTypes;
    private Integer priceRange;
    private List<OpeningHours> openingHours;
    private List<Table> tables;
    private List<String> images;
    private String managerId;
    private Date createdAt;
    private boolean approved;
    private boolean active;
    private double averageRating;
    private int totalReviews;
    private int totalBookingsToday;
}