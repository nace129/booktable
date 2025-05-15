package com.booktable.api.controller;

import com.booktable.api.dto.ReservationCreateRequest;
import com.booktable.api.dto.ReservationResponse;
import com.booktable.api.dto.ReservationUpdateRequest;
import com.booktable.api.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Page<ReservationResponse>> getUserReservations(Pageable pageable) {
        return ResponseEntity.ok(reservationService.getUserReservations(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_RESTAURANT_MANAGER', 'ROLE_ADMIN')")
    public ResponseEntity<ReservationResponse> getReservationById(@PathVariable String id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<ReservationResponse> createReservation(@Valid @RequestBody ReservationCreateRequest request) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_RESTAURANT_MANAGER')")
    public ResponseEntity<ReservationResponse> updateReservation(
            @PathVariable String id,
            @Valid @RequestBody ReservationUpdateRequest request) {
        return ResponseEntity.ok(reservationService.updateReservation(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Void> cancelReservation(@PathVariable String id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasRole('ROLE_RESTAURANT_MANAGER')")
    public ResponseEntity<Page<ReservationResponse>> getRestaurantReservations(
            @PathVariable String restaurantId, Pageable pageable) {
        return ResponseEntity.ok(reservationService.getRestaurantReservations(restaurantId, pageable));
    }
}