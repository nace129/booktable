package com.booktable.api.dto;

import com.booktable.api.model.Address;
import com.booktable.api.model.OpeningHours;
import com.booktable.api.model.Table;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantUpdateRequest {
    
    private String name;
    private String description;
    
    @Valid
    private Address address;
    
    private String phoneNumber;
    private String email;
    private String website;
    private List<String> cuisineTypes;
    private Integer priceRange;
    private List<OpeningHours> openingHours;
    private List<Table> tables;
    private List<String> images;
    private Boolean active;
}