package com.booktable.api.repository;

import com.booktable.api.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    
    Page<Review> findByRestaurantIdOrderByCreatedAtDesc(String restaurantId, Pageable pageable);
    
    List<Review> findByUserId(String userId);
    
    Optional<Review> findByUserIdAndReservationId(String userId, String reservationId);
    
    double countByRestaurantIdAndRating(String restaurantId, int rating);
}