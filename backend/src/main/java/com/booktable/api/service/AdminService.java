package com.booktable.api.service;

import com.booktable.api.dto.AnalyticsResponse;
import com.booktable.api.dto.UserResponse;
import com.booktable.api.exception.BadRequestException;
import com.booktable.api.exception.ResourceNotFoundException;
import com.booktable.api.model.Reservation;
import com.booktable.api.model.Restaurant;
import com.booktable.api.model.User;
import com.booktable.api.repository.ReservationRepository;
import com.booktable.api.repository.RestaurantRepository;
import com.booktable.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AnalyticsResponse getReservationAnalytics(String restaurantId) {
        // Get date range for the last 30 days
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        
        // Get all reservations in the date range
        List<Reservation> reservations;
        if (restaurantId != null && !restaurantId.isEmpty()) {
            reservations = reservationRepository.findByRestaurantIdAndDateRange(restaurantId, startDate, endDate);
        } else {
            reservations = reservationRepository.findByStatusAndFutureDate(null, startDate);
        }
        
        // Count reservations by status
        long totalReservations = reservations.size();
        long confirmedReservations = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
                .count();
        long cancelledReservations = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.CANCELLED)
                .count();
        long completedReservations = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.COMPLETED)
                .count();
        long noShowReservations = reservations.stream()
                .filter(r -> r.getStatus() == Reservation.ReservationStatus.NO_SHOW)
                .count();
        
        // Group reservations by day
        Map<String, Long> reservationsPerDay = new HashMap<>();
        for (int i = 0; i < 30; i++) {
            LocalDate day = endDate.minusDays(i).toLocalDate();
            String formattedDay = day.format(DATE_FORMATTER);
            reservationsPerDay.put(formattedDay, 0L);
        }
        
        for (Reservation reservation : reservations) {
            LocalDate reservationDate = reservation.getReservationDateTime().toLocalDate();
            String formattedDate = reservationDate.format(DATE_FORMATTER);
            
            if (reservationsPerDay.containsKey(formattedDate)) {
                reservationsPerDay.put(formattedDate, reservationsPerDay.get(formattedDate) + 1);
            }
        }
        
        // Group reservations by restaurant (top 10)
        Map<String, Long> reservationsByRestaurant = reservations.stream()
                .collect(Collectors.groupingBy(Reservation::getRestaurantId, Collectors.counting()));
        
        Map<String, Long> topRestaurants = new HashMap<>();
        
        if (!reservationsByRestaurant.isEmpty()) {
            reservationsByRestaurant.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .forEach(entry -> {
                        String restaurantName = restaurantRepository.findById(entry.getKey())
                                .map(Restaurant::getName)
                                .orElse("Unknown Restaurant");
                        topRestaurants.put(restaurantName, entry.getValue());
                    });
        }
        
        // Group reservations by cuisine type
        Map<String, Long> reservationsByCuisine = new HashMap<>();
        
        for (Reservation reservation : reservations) {
            Restaurant restaurant = restaurantRepository.findById(reservation.getRestaurantId()).orElse(null);
            if (restaurant != null && restaurant.getCuisineTypes() != null) {
                for (String cuisine : restaurant.getCuisineTypes()) {
                    reservationsByCuisine.put(cuisine, 
                            reservationsByCuisine.getOrDefault(cuisine, 0L) + 1);
                }
            }
        }
        
        return AnalyticsResponse.builder()
                .totalReservations(totalReservations)
                .confirmedReservations(confirmedReservations)
                .cancelledReservations(cancelledReservations)
                .completedReservations(completedReservations)
                .noShowReservations(noShowReservations)
                .reservationsPerDay(reservationsPerDay)
                .topRestaurants(topRestaurants)
                .reservationsByCuisine(reservationsByCuisine)
                .build();
    }

    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToUserResponse);
    }

    public UserResponse addRoleToUser(String userId, String role) {
        User user = findUserById(userId);
        
        String formattedRole = formatRole(role);
        
        if (user.getRoles().contains(formattedRole)) {
            throw new BadRequestException("User already has the role: " + formattedRole);
        }
        
        List<String> roles = new ArrayList<>(user.getRoles());
        roles.add(formattedRole);
        user.setRoles(roles);
        
        return mapToUserResponse(userRepository.save(user));
    }

    public UserResponse removeRoleFromUser(String userId, String role) {
        User user = findUserById(userId);
        
        String formattedRole = formatRole(role);
        
        if (!user.getRoles().contains(formattedRole)) {
            throw new BadRequestException("User does not have the role: " + formattedRole);
        }
        
        // Ensure user has at least one role
        if (user.getRoles().size() <= 1) {
            throw new BadRequestException("Cannot remove the only role from a user");
        }
        
        List<String> roles = new ArrayList<>(user.getRoles());
        roles.remove(formattedRole);
        user.setRoles(roles);
        
        return mapToUserResponse(userRepository.save(user));
    }

    public UserResponse disableUser(String userId) {
        User user = findUserById(userId);
        user.setEnabled(false);
        return mapToUserResponse(userRepository.save(user));
    }

    public UserResponse enableUser(String userId) {
        User user = findUserById(userId);
        user.setEnabled(true);
        return mapToUserResponse(userRepository.save(user));
    }

    private User findUserById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .enabled(user.isEnabled())
                .build();
    }
    
    private String formatRole(String role) {
        if (!role.startsWith("ROLE_")) {
            return "ROLE_" + role.toUpperCase();
        }
        return role.toUpperCase();
    }
}