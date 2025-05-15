package com.booktable.api.service;

import com.booktable.api.dto.ReviewCreateRequest;
import com.booktable.api.dto.ReviewResponse;
import com.booktable.api.dto.ReviewUpdateRequest;
import com.booktable.api.exception.BadRequestException;
import com.booktable.api.exception.ResourceNotFoundException;
import com.booktable.api.model.Reservation;
import com.booktable.api.model.Restaurant;
import com.booktable.api.model.Review;
import com.booktable.api.model.User;
import com.booktable.api.repository.ReservationRepository;
import com.booktable.api.repository.RestaurantRepository;
import com.booktable.api.repository.ReviewRepository;
import com.booktable.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final ReservationRepository reservationRepository;

    public Page<ReviewResponse> getRestaurantReviews(String restaurantId, Pageable pageable) {
        return reviewRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurantId, pageable)
                .map(this::mapToReviewResponse);
    }

    @Transactional
    public ReviewResponse createReview(ReviewCreateRequest request) {
        User currentUser = getCurrentUser();
        
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
        
        if (!reservation.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only review your own reservations");
        }
        
        if (!reservation.getRestaurantId().equals(request.getRestaurantId())) {
            throw new BadRequestException("Reservation is not for the specified restaurant");
        }
        
        Optional<Review> existingReview = reviewRepository.findByUserIdAndReservationId(
                currentUser.getId(), reservation.getId());
        
        if (existingReview.isPresent()) {
            throw new BadRequestException("You have already reviewed this reservation");
        }
        
        Review review = Review.builder()
                .restaurantId(request.getRestaurantId())
                .userId(currentUser.getId())
                .reservationId(request.getReservationId())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(new Date())
                .build();
        
        Review savedReview = reviewRepository.save(review);
        
        updateRestaurantRating(restaurant);
        
        return mapToReviewResponse(savedReview);
    }

    @Transactional
    public ReviewResponse updateReview(String id, ReviewUpdateRequest request) {
        User currentUser = getCurrentUser();
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        if (!review.getUserId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only update your own reviews");
        }
        
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }
        
        if (request.getComment() != null) {
            review.setComment(request.getComment());
        }
        
        review.setUpdatedAt(new Date());
        Review updatedReview = reviewRepository.save(review);
        
        Restaurant restaurant = restaurantRepository.findById(review.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        updateRestaurantRating(restaurant);
        
        return mapToReviewResponse(updatedReview);
    }

    @Transactional
    public void deleteReview(String id) {
        User currentUser = getCurrentUser();
        
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));
        
        boolean isAdmin = currentUser.getRoles().contains("ROLE_ADMIN");
        boolean isOwner = review.getUserId().equals(currentUser.getId());
        
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You don't have permission to delete this review");
        }
        
        reviewRepository.delete(review);
        
        Restaurant restaurant = restaurantRepository.findById(review.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        updateRestaurantRating(restaurant);
    }

    public Page<ReviewResponse> getUserReviews(Pageable pageable) {
        User currentUser = getCurrentUser();
        
        List<Review> userReviews = reviewRepository.findByUserId(currentUser.getId());
        List<ReviewResponse> reviewResponses = userReviews.stream()
                .map(this::mapToReviewResponse)
                .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), reviewResponses.size());
        
        return new PageImpl<>(
            reviewResponses.subList(start, end),
            pageable,
            reviewResponses.size()
        );
    }

    private ReviewResponse mapToReviewResponse(Review review) {
        Restaurant restaurant = restaurantRepository.findById(review.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        User user = userRepository.findById(review.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return ReviewResponse.builder()
                .id(review.getId())
                .restaurantId(review.getRestaurantId())
                .restaurantName(restaurant.getName())
                .userId(review.getUserId())
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .reservationId(review.getReservationId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
    
    private void updateRestaurantRating(Restaurant restaurant) {
        double totalRating = 0;
        int count = 0;
        
        for (int i = 1; i <= 5; i++) {
            double countForRating = reviewRepository.countByRestaurantIdAndRating(restaurant.getId(), i);
            totalRating += i * countForRating;
            count += countForRating;
        }
        
        double averageRating = count > 0 ? totalRating / count : 0;
        
        restaurant.setAverageRating(averageRating);
        restaurant.setTotalReviews(count);
        restaurantRepository.save(restaurant);
    }
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}