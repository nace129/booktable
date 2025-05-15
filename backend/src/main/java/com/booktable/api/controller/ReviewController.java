package com.booktable.api.controller;

import com.booktable.api.dto.ReviewCreateRequest;
import com.booktable.api.dto.ReviewResponse;
import com.booktable.api.dto.ReviewUpdateRequest;
import com.booktable.api.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/public/restaurant/{restaurantId}")
    public ResponseEntity<Page<ReviewResponse>> getRestaurantReviews(
            @PathVariable String restaurantId, Pageable pageable) {
        return ResponseEntity.ok(reviewService.getRestaurantReviews(restaurantId, pageable));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewCreateRequest request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<ReviewResponse> updateReview(
            @PathVariable String id,
            @Valid @RequestBody ReviewUpdateRequest request) {
        return ResponseEntity.ok(reviewService.updateReview(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_CUSTOMER', 'ROLE_ADMIN')")
    public ResponseEntity<Void> deleteReview(@PathVariable String id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('ROLE_CUSTOMER')")
    public ResponseEntity<Page<ReviewResponse>> getUserReviews(Pageable pageable) {
        return ResponseEntity.ok(reviewService.getUserReviews(pageable));
    }
}