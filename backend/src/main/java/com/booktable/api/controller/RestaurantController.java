package com.booktable.api.controller;

import com.booktable.api.dto.RestaurantCreateRequest;
import com.booktable.api.dto.RestaurantResponse;
import com.booktable.api.dto.RestaurantSearchRequest;
import com.booktable.api.dto.RestaurantUpdateRequest;
import com.booktable.api.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping("/public")
    public ResponseEntity<Page<RestaurantResponse>> getAllRestaurants(Pageable pageable) {
        return ResponseEntity.ok(restaurantService.getAllApprovedRestaurants(pageable));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @PostMapping("/public/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(@Valid @RequestBody RestaurantSearchRequest request) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(request));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_RESTAURANT_MANAGER')")
    public ResponseEntity<RestaurantResponse> createRestaurant(@Valid @RequestBody RestaurantCreateRequest request) {
        return ResponseEntity.ok(restaurantService.createRestaurant(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_RESTAURANT_MANAGER') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<RestaurantResponse> updateRestaurant(
            @PathVariable String id,
            @Valid @RequestBody RestaurantUpdateRequest request) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/manager")
    @PreAuthorize("hasRole('ROLE_RESTAURANT_MANAGER')")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByManager() {
        return ResponseEntity.ok(restaurantService.getRestaurantsByManager());
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<RestaurantResponse> approveRestaurant(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.approveRestaurant(id));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<RestaurantResponse>> getPendingRestaurants(Pageable pageable) {
        return ResponseEntity.ok(restaurantService.getPendingRestaurants(pageable));
    }
}