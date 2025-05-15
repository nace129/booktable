package com.booktable.api.dto;

import com.booktable.api.model.Address;
import com.booktable.api.model.OpeningHours;
import com.booktable.api.model.Table;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantCreateRequest {
    
    @NotBlank(message = "Restaurant name is required")
    private String name;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Address is required")
    @Valid
    private Address address;
    
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    
    private String email;
    
    private String website;
    
    @NotEmpty(message = "At least one cuisine type is required")
    private List<String> cuisineTypes;
    
    @NotNull(message = "Price range is required")
    private Integer priceRange;
    
    @NotEmpty(message = "Opening hours are required")
    private List<OpeningHours> openingHours;
    
    @NotEmpty(message = "Tables are required")
    private List<Table> tables;
    
    private List<String> images;
}