package com.booktable.api.service;

import com.booktable.api.dto.ReservationCreateRequest;
import com.booktable.api.dto.ReservationResponse;
import com.booktable.api.dto.ReservationUpdateRequest;
import com.booktable.api.exception.BadRequestException;
import com.booktable.api.exception.ResourceNotFoundException;
import com.booktable.api.model.Reservation;
import com.booktable.api.model.Reservation.ReservationStatus;
import com.booktable.api.model.Restaurant;
import com.booktable.api.model.Table;
import com.booktable.api.model.User;
import com.booktable.api.repository.ReservationRepository;
import com.booktable.api.repository.RestaurantRepository;
import com.booktable.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ReservationService.class);

    private final ReservationRepository reservationRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public Page<ReservationResponse> getUserReservations(Pageable pageable) {
        User currentUser = getCurrentUser();
        return reservationRepository.findByUserIdOrderByReservationDateTimeDesc(currentUser.getId(), pageable)
                .map(this::mapToReservationResponse);
    }

    public ReservationResponse getReservationById(String id) {
        Reservation reservation = findReservationById(id);
        
        // Check if current user has access to this reservation
        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRoles().contains("ROLE_ADMIN");
        boolean isCustomer = currentUser.getId().equals(reservation.getUserId());
        
        // log.info("Looking up restaurant: {}", request.getRestaurantId());
        // Restaurant restaurant = restaurantRepository.findById(reservation.getRestaurantId())
        //         .orElseThrow(() -> {log.error("Restaurant not found: {}", request.getRestaurantId()); return ResourceNotFoundException("Restaurant not found")});

        Restaurant restaurant = restaurantRepository.findByCustomRestaurantId(reservation.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        boolean isRestaurantManager = currentUser.getId().equals(restaurant.getManagerId());
        
        if (!isAdmin && !isCustomer && !isRestaurantManager) {
            throw new AccessDeniedException("You don't have permission to view this reservation");
        }
        
        return mapToReservationResponse(reservation);
    }
    
    public ReservationResponse createReservation(ReservationCreateRequest request) {
    User currentUser = getCurrentUser();

    log.info("Looking up restaurant: {}", request.getRestaurantId());

    Restaurant restaurant = restaurantRepository.findByRestaurantExternalId(request.getRestaurantId())
            .orElseThrow(() -> {
                log.error("Restaurant not found: {}", request.getRestaurantId());
                return new ResourceNotFoundException("Restaurant not found");
            });

        

    if (!restaurant.isApproved() || !restaurant.isActive()) {
        throw new BadRequestException("Restaurant is not available for reservations");
    }

    if (request.getReservationDateTime().isBefore(LocalDateTime.now())) {
        throw new BadRequestException("Reservation time must be in the future");
    }

    log.info("Checking table availability for restaurant={}, partySize={}, time={}",
            restaurant.getId(), request.getPartySize(), request.getReservationDateTime());

    Optional<Table> availableTable = findAvailableTable(
            restaurant,
            request.getReservationDateTime(),
            request.getPartySize());

    if (availableTable.isEmpty()) {
        log.warn("No available tables found for time={} and partySize={}",
                request.getReservationDateTime(), request.getPartySize());
        throw new BadRequestException("No available tables for the requested time and party size");
    }

    Reservation reservation = Reservation.builder()
            .restaurantId(restaurant.getId())
            .userId(currentUser.getId())
            .tableId(availableTable.get().getId())
            .reservationDateTime(request.getReservationDateTime())
            .partySize(request.getPartySize())
            .specialRequests(request.getSpecialRequests())
            .status(ReservationStatus.CONFIRMED)
            .createdAt(new Date())
            .build();

    Reservation savedReservation = reservationRepository.save(reservation);
    log.info("Reservation saved: {}", savedReservation.getId());

    // Safely increment booking count
    if (savedReservation.getReservationDateTime().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
        Integer currentCount = restaurant.getTotalBookingsToday();
        if (currentCount == null) {
            currentCount = 0;
        }
        restaurant.setTotalBookingsToday(currentCount + 1);
        restaurantRepository.save(restaurant);
        log.info("Updated total bookings today for restaurant {}", restaurant.getId());
    }

    // Send email (non-blocking)
    try {
        sendConfirmationEmail(savedReservation, restaurant, currentUser);
    } catch (Exception e) {
        log.warn("Failed to send confirmation email for reservation {}", savedReservation.getId(), e);
    }

    return mapToReservationResponse(savedReservation);
}

//     public ReservationResponse createReservation(ReservationCreateRequest request) {
//         User currentUser = getCurrentUser();
        
//         log.info("Looking up restaurant: {}", request.getRestaurantId());
//         // Find the restaurant2
//         Restaurant restaurant = restaurantRepository.findByRestaurantExternalId(request.getRestaurantId())
//                 .orElseThrow(() -> {log.error("Restaurant not found: {}", request.getRestaurantId()); return new ResourceNotFoundException("Restaurant not found");});
        
//         if (!restaurant.isApproved() || !restaurant.isActive()) {
//             throw new BadRequestException("Restaurant is not available for reservations");
//         }
        
//         // Check if reservation time is in the future
//         if (request.getReservationDateTime().isBefore(LocalDateTime.now())) {
//             throw new BadRequestException("Reservation time must be in the future");
//         }
        
//         log.info("Checking table availability for restaurant={}, partySize={}, time={}",
//          restaurant.getId());

//         // Find an available table that can accommodate the party
//         Optional<Table> availableTable = findAvailableTable(
//                 restaurant, 
//                 request.getReservationDateTime(), 
//                 request.getPartySize());
        
//         if (availableTable.isEmpty()) {
//             log.warn("No available tables found for time={} and partySize={}");
//             throw new BadRequestException("No available tables for the requested time and party size");
//         }
        
//         // Create the reservation
//         Reservation reservation = Reservation.builder()
//                 .restaurantId(restaurant.getId())
//                 .userId(currentUser.getId())
//                 .tableId(availableTable.get().getId())
//                 .reservationDateTime(request.getReservationDateTime())
//                 .partySize(request.getPartySize())
//                 .specialRequests(request.getSpecialRequests())
//                 .status(ReservationStatus.CONFIRMED)
//                 .createdAt(new Date())
//                 .build();
        
//         Reservation savedReservation = reservationRepository.save(reservation);
        
//         // Update restaurant's booking count for today
//         if (savedReservation.getReservationDateTime().toLocalDate().equals(LocalDateTime.now().toLocalDate())) {
//             restaurant.setTotalBookingsToday(restaurant.getTotalBookingsToday() + 1);
//             restaurantRepository.save(restaurant);
//         }
        
//         // Send confirmation email
//         try
//         {
//             sendConfirmationEmail(savedReservation, restaurant, currentUser);
//         }
//         catch (Exception e) {
//         log.warn("Failed to send email for reservation {}, but continuing", reservation.getId(), e);
// }

        
//         return mapToReservationResponse(savedReservation);
//     }

    public ReservationResponse updateReservation(String id, ReservationUpdateRequest request) {
        Reservation reservation = findReservationById(id);
        User currentUser = getCurrentUser();
        
        // Check if current user has access to modify this reservation
        boolean isCustomer = currentUser.getId().equals(reservation.getUserId());
        
        Restaurant restaurant = restaurantRepository.findById(reservation.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        boolean isRestaurantManager = currentUser.getId().equals(restaurant.getManagerId());
        
        if (!isCustomer && !isRestaurantManager) {
            throw new AccessDeniedException("You don't have permission to update this reservation");
        }
        
        // Customers can only update certain fields
        if (isCustomer && !isRestaurantManager) {
            // Customers can't change status directly to COMPLETED or NO_SHOW
            if (request.getStatus() != null && 
                (request.getStatus() == ReservationStatus.COMPLETED || 
                 request.getStatus() == ReservationStatus.NO_SHOW)) {
                throw new BadRequestException("You don't have permission to set this status");
            }
            
            // If changing time/party size, need to check table availability
            if ((request.getReservationDateTime() != null && !request.getReservationDateTime().equals(reservation.getReservationDateTime())) ||
                (request.getPartySize() != null && request.getPartySize() != reservation.getPartySize())) {
                
                LocalDateTime newDateTime = request.getReservationDateTime() != null ? 
                        request.getReservationDateTime() : reservation.getReservationDateTime();
                        
                int newPartySize = request.getPartySize() != null ? 
                        request.getPartySize() : reservation.getPartySize();
                
                // Find an available table for the new time and party size
                Optional<Table> availableTable = findAvailableTable(restaurant, newDateTime, newPartySize);
                
                if (availableTable.isEmpty()) {
                    throw new BadRequestException("No available tables for the requested time and party size");
                }
                
                // Update the table ID if a different table is assigned
                reservation.setTableId(availableTable.get().getId());
            }
        }
        
        // Update fields if provided
        if (request.getReservationDateTime() != null) {
            reservation.setReservationDateTime(request.getReservationDateTime());
        }
        if (request.getPartySize() != null) {
            reservation.setPartySize(request.getPartySize());
        }
        if (request.getSpecialRequests() != null) {
            reservation.setSpecialRequests(request.getSpecialRequests());
        }
        if (request.getStatus() != null) {
            reservation.setStatus(request.getStatus());
        }
        
        reservation.setUpdatedAt(new Date());
        return mapToReservationResponse(reservationRepository.save(reservation));
    }

    public void cancelReservation(String id) {
        Reservation reservation = findReservationById(id);
        User currentUser = getCurrentUser();
        
        // Only the reservation owner can cancel
        if (!currentUser.getId().equals(reservation.getUserId())) {
            throw new AccessDeniedException("You don't have permission to cancel this reservation");
        }
        
        // Can only cancel if not already completed or cancelled
        if (reservation.getStatus() == ReservationStatus.COMPLETED || 
            reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BadRequestException("Cannot cancel a reservation that is already " + reservation.getStatus());
        }
        
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setUpdatedAt(new Date());
        reservationRepository.save(reservation);
        
        // Send cancellation email
        sendCancellationEmail(reservation);
    }

    public Page<ReservationResponse> getRestaurantReservations(String restaurantId, Pageable pageable) {
        User currentUser = getCurrentUser();
        
        // Check if user is admin or restaurant manager
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        boolean isAdmin = currentUser.getRoles().contains("ROLE_ADMIN");
        boolean isManager = currentUser.getId().equals(restaurant.getManagerId());
        
        if (!isAdmin && !isManager) {
            throw new AccessDeniedException("You don't have permission to view reservations for this restaurant");
        }
        
        return reservationRepository.findByRestaurantIdOrderByReservationDateTimeDesc(restaurantId, pageable)
                .map(this::mapToReservationResponse);
    }

    private Reservation findReservationById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found with id: " + id));
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        // Get restaurant and user details
        Restaurant restaurant = restaurantRepository.findById(reservation.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        User user = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        return ReservationResponse.builder()
                .id(reservation.getId())
                .restaurantId(reservation.getRestaurantId())
                .restaurantName(restaurant.getName())
                .userId(reservation.getUserId())
                .userFullName(user.getFirstName() + " " + user.getLastName())
                .tableId(reservation.getTableId())
                .reservationDateTime(reservation.getReservationDateTime())
                .partySize(reservation.getPartySize())
                .specialRequests(reservation.getSpecialRequests())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
    }
    
    private Optional<Table> findAvailableTable(Restaurant restaurant, LocalDateTime reservationTime, int partySize) {
        // Find tables that can accommodate the party size
        List<Table> suitableTables = restaurant.getTables().stream()
                .filter(table -> table.getCapacity() >= partySize && table.isAvailable())
                .toList();
        
        // Check each table for existing reservations at the requested time
        LocalDateTime windowStart = reservationTime.minusHours(2);
        LocalDateTime windowEnd = reservationTime.plusHours(2);
        
        for (Table table : suitableTables) {
            List<Reservation> existingReservations = 
                    reservationRepository.findReservationsForTableInTimeRange(
                            restaurant.getId(), 
                            table.getId(), 
                            windowStart, 
                            windowEnd);
            
            if (existingReservations.isEmpty()) {
                return Optional.of(table);
            }
        }
        
        return Optional.empty();
    }
    
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
    
    private void sendConfirmationEmail(Reservation reservation, Restaurant restaurant, User user) {
        String subject = "Reservation Confirmation - " + restaurant.getName();
        String body = String.format(
                "Hello %s,\n\n" +
                "Your reservation at %s has been confirmed for %s.\n" +
                "Reservation details:\n" +
                "- Date and Time: %s\n" +
                "- Party Size: %d\n" +
                "- Reservation ID: %s\n\n" +
                "If you need to make any changes, please login to your account.\n\n" +
                "Thank you for choosing %s!\n\n" +
                "Best regards,\n" +
                "The BookTable Team",
                user.getFirstName(),
                restaurant.getName(),
                reservation.getReservationDateTime().toString(),
                reservation.getReservationDateTime().toString(),
                reservation.getPartySize(),
                reservation.getId(),
                restaurant.getName()
        );
        
        emailService.sendEmail(user.getEmail(), subject, body);
    }
    
    private void sendCancellationEmail(Reservation reservation) {
        User user = userRepository.findById(reservation.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        Restaurant restaurant = restaurantRepository.findById(reservation.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant not found"));
        
        String subject = "Reservation Cancellation - " + restaurant.getName();
        String body = String.format(
                "Hello %s,\n\n" +
                "Your reservation at %s for %s has been cancelled.\n\n" +
                "If you did not request this cancellation, please contact us immediately.\n\n" +
                "Best regards,\n" +
                "The BookTable Team",
                user.getFirstName(),
                restaurant.getName(),
                reservation.getReservationDateTime().toString()
        );
        
        emailService.sendEmail(user.getEmail(), subject, body);
    }
}