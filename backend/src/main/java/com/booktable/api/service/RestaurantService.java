package com.booktable.api.service;

import com.booktable.api.dto.RestaurantCreateRequest;
import com.booktable.api.dto.RestaurantResponse;
import com.booktable.api.dto.RestaurantSearchRequest;
import com.booktable.api.dto.RestaurantUpdateRequest;
import com.booktable.api.exception.AccessDeniedException;
import com.booktable.api.exception.ResourceNotFoundException;
import com.booktable.api.model.OpeningHours;
import com.booktable.api.model.Restaurant;
import com.booktable.api.model.Table;
import com.booktable.api.model.User;
import com.booktable.api.repository.ReservationRepository;
import com.booktable.api.repository.RestaurantRepository;
import com.booktable.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    public Page<RestaurantResponse> getAllApprovedRestaurants(Pageable pageable) {
        return restaurantRepository.findByApprovedIsTrueAndActiveIsTrue(pageable)
                .map(this::mapToRestaurantResponse);
    }

    public RestaurantResponse getRestaurantById(String id) {
        Restaurant restaurant = findRestaurantById(id);
        if (!restaurant.isApproved() && !isAdminOrOwner(restaurant.getManagerId())) {
            throw new AccessDeniedException("You don't have permission to view this restaurant");
        }
        return mapToRestaurantResponse(restaurant);
    }

    public RestaurantResponse createRestaurant(RestaurantCreateRequest request) {
        User currentUser = getCurrentUser();
        
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .website(request.getWebsite())
                .cuisineTypes(request.getCuisineTypes())
                .priceRange(request.getPriceRange())
                .openingHours(request.getOpeningHours())
                .managerId(currentUser.getId())
                .approved(false)
                .active(true)
                .build();

        // Set unique IDs for each table
        List<Table> tables = request.getTables().stream()
                .map(table -> Table.builder()
                        .id(UUID.randomUUID().toString())
                        .name(table.getName())
                        .capacity(table.getCapacity())
                        .isAvailable(true)
                        .build())
                .collect(Collectors.toList());
        restaurant.setTables(tables);

        // Add images if provided
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            restaurant.setImages(request.getImages());
        }

        return mapToRestaurantResponse(restaurantRepository.save(restaurant));
    }

    public RestaurantResponse updateRestaurant(String id, RestaurantUpdateRequest request) {
        Restaurant restaurant = findRestaurantById(id);
        
        // Check if current user is allowed to update this restaurant
        if (!isAdminOrOwner(restaurant.getManagerId())) {
            throw new AccessDeniedException("You don't have permission to update this restaurant");
        }

        // Update fields if provided
        if (request.getName() != null) {
            restaurant.setName(request.getName());
        }
        if (request.getDescription() != null) {
            restaurant.setDescription(request.getDescription());
        }
        if (request.getAddress() != null) {
            restaurant.setAddress(request.getAddress());
        }
        if (request.getPhoneNumber() != null) {
            restaurant.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getEmail() != null) {
            restaurant.setEmail(request.getEmail());
        }
        if (request.getWebsite() != null) {
            restaurant.setWebsite(request.getWebsite());
        }
        if (request.getCuisineTypes() != null) {
            restaurant.setCuisineTypes(request.getCuisineTypes());
        }
        if (request.getPriceRange() != null) {
            restaurant.setPriceRange(request.getPriceRange());
        }
        if (request.getOpeningHours() != null) {
            restaurant.setOpeningHours(request.getOpeningHours());
        }
        if (request.getTables() != null) {
            // Update existing tables and add new ones with unique IDs
            List<Table> updatedTables = request.getTables().stream()
                    .map(table -> {
                        if (table.getId() == null) {
                            return Table.builder()
                                    .id(UUID.randomUUID().toString())
                                    .name(table.getName())
                                    .capacity(table.getCapacity())
                                    .isAvailable(true)
                                    .build();
                        } else {
                            return table;
                        }
                    })
                    .collect(Collectors.toList());
            restaurant.setTables(updatedTables);
        }
        if (request.getImages() != null) {
            restaurant.setImages(request.getImages());
        }
        if (request.getActive() != null) {
            restaurant.setActive(request.getActive());
        }

        return mapToRestaurantResponse(restaurantRepository.save(restaurant));
    }

    public void deleteRestaurant(String id) {
        Restaurant restaurant = findRestaurantById(id);
        
        // Soft delete by setting active to false
        restaurant.setActive(false);
        restaurantRepository.save(restaurant);
    }

    public RestaurantResponse approveRestaurant(String id) {
        Restaurant restaurant = findRestaurantById(id);
        restaurant.setApproved(true);
        return mapToRestaurantResponse(restaurantRepository.save(restaurant));
    }

    public List<RestaurantResponse> getRestaurantsByManager() {
        User currentUser = getCurrentUser();
        return restaurantRepository.findByManagerId(currentUser.getId()).stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    public Page<RestaurantResponse> getPendingRestaurants(Pageable pageable) {
        return restaurantRepository.findByApprovedIsFalseAndActiveIsTrue(pageable)
                .map(this::mapToRestaurantResponse);
    }

    public List<RestaurantResponse> searchRestaurants(RestaurantSearchRequest request) {
        // First, find restaurants by location if provided
        List<Restaurant> restaurantList = new ArrayList<>();
        
        if (request.getZipCode() != null && !request.getZipCode().isEmpty()) {
            restaurantList.addAll(restaurantRepository.findByZipCode(request.getZipCode(), Pageable.unpaged()).getContent());
        } else if (request.getCity() != null && !request.getCity().isEmpty()) {
            restaurantList.addAll(restaurantRepository.findByCity(request.getCity(), Pageable.unpaged()).getContent());
        } else if (request.getState() != null && !request.getState().isEmpty()) {
            restaurantList.addAll(restaurantRepository.findByState(request.getState(), Pageable.unpaged()).getContent());
        } else {
            // If no location filter, get all approved restaurants
            restaurantList.addAll(restaurantRepository.findByApprovedIsTrueAndActiveIsTrue(Pageable.unpaged()).getContent());
        }
        
        // Filter by cuisine type if provided
        if (request.getCuisineType() != null && !request.getCuisineType().isEmpty()) {
            restaurantList = restaurantList.stream()
                    .filter(r -> r.getCuisineTypes().contains(request.getCuisineType()))
                    .collect(Collectors.toList());
        }
        
        // Get the day of week for the requested date
        DayOfWeek dayOfWeek = request.getDate().getDayOfWeek();
        LocalTime requestedTime = request.getTime();
        
        // Filter restaurants by opening hours
        restaurantList = restaurantList.stream()
                .filter(restaurant -> restaurant.getOpeningHours().stream()
                        .anyMatch(hours -> hours.getDayOfWeek() == dayOfWeek &&
                                requestedTime.isAfter(hours.getOpenTime()) &&
                                requestedTime.isBefore(hours.getCloseTime())))
                .collect(Collectors.toList());
        
        // Filter restaurants by table availability
        LocalDateTime requestDateTime = LocalDateTime.of(request.getDate(), requestedTime);
        LocalDateTime searchWindowStart = requestDateTime.minusMinutes(30);
        LocalDateTime searchWindowEnd = requestDateTime.plusMinutes(30);
        
        List<RestaurantResponse> availableRestaurants = new ArrayList<>();
        
        for (Restaurant restaurant : restaurantList) {
            // Find tables that can accommodate the party size
            List<Table> suitableTables = restaurant.getTables().stream()
                    .filter(table -> table.getCapacity() >= request.getPartySize())
                    .collect(Collectors.toList());
            
            // Check if any of these tables are available in the time window
            boolean hasAvailableTables = false;
            
            for (Table table : suitableTables) {
                // Check reservations for this table in the time window
                List<com.booktable.api.model.Reservation> existingReservations = 
                        reservationRepository.findReservationsForTableInTimeRange(
                                restaurant.getId(), 
                                table.getId(), 
                                searchWindowStart, 
                                searchWindowEnd);
                
                if (existingReservations.isEmpty()) {
                    hasAvailableTables = true;
                    break;
                }
            }
            
            if (hasAvailableTables) {
                // Add this restaurant to the available list
                availableRestaurants.add(mapToRestaurantResponse(restaurant));
            }
        }
        
        return availableRestaurants;
    }

    private Restaurant findRestaurantById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found with id: " + id));
    }

    private RestaurantResponse mapToRestaurantResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .description(restaurant.getDescription())
                .address(restaurant.getAddress())
                .phoneNumber(restaurant.getPhoneNumber())
                .email(restaurant.getEmail())
                .website(restaurant.getWebsite())
                .cuisineTypes(restaurant.getCuisineTypes())
                .priceRange(restaurant.getPriceRange())
                .openingHours(restaurant.getOpeningHours())
                .tables(restaurant.getTables())
                .images(restaurant.getImages())
                .managerId(restaurant.getManagerId())
                .createdAt(restaurant.getCreatedAt())
                .approved(restaurant.isApproved())
                .active(restaurant.isActive())
                .averageRating(restaurant.getAverageRating())
                .totalReviews(restaurant.getTotalReviews())
                .totalBookingsToday(restaurant.getTotalBookingsToday())
                .build();
    }
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    private boolean isAdminOrOwner(String managerId) {
        User currentUser = getCurrentUser();
        return currentUser.getRoles().contains("ROLE_ADMIN") || 
               currentUser.getId().equals(managerId);
    }
}