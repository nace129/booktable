package com.booktable.api.repository;

import com.booktable.api.model.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends MongoRepository<Restaurant, String> {
    
    
    Page<Restaurant> findByApprovedIsTrueAndActiveIsTrue(Pageable pageable);
    
    Page<Restaurant> findByApprovedIsFalseAndActiveIsTrue(Pageable pageable);
    
    List<Restaurant> findByManagerId(String managerId);
    
    @Query("{'address.city': ?0, 'approved': true, 'active': true}")
    Page<Restaurant> findByCity(String city, Pageable pageable);
    
    @Query("{'address.state': ?0, 'approved': true, 'active': true}")
    Page<Restaurant> findByState(String state, Pageable pageable);
    
    @Query("{'address.zipCode': ?0, 'approved': true, 'active': true}")
    Page<Restaurant> findByZipCode(String zipCode, Pageable pageable);
    
    @Query("{'cuisineTypes': {$in: [?0]}, 'approved': true, 'active': true}")
    Page<Restaurant> findByCuisineType(String cuisineType, Pageable pageable);

    @Query("{ id: ?0 }")
    Optional<Restaurant> findByCustomRestaurantId(String id);

    @Query("{ 'id': ?0 }")
    Optional<Restaurant> findByRestaurantExternalId(String id);

    // Restaurant findByCustomId(String id);

}