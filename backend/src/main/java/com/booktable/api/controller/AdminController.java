package com.booktable.api.controller;

import com.booktable.api.dto.AnalyticsResponse;
import com.booktable.api.dto.UserResponse;
import com.booktable.api.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/analytics/reservations")
    public ResponseEntity<AnalyticsResponse> getReservationAnalytics(
            @RequestParam(required = false) String restaurantId) {
        return ResponseEntity.ok(adminService.getReservationAnalytics(restaurantId));
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserResponse>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllUsers(pageable));
    }

    @PostMapping("/users/{id}/role/{role}")
    public ResponseEntity<UserResponse> addRoleToUser(
            @PathVariable String id, 
            @PathVariable String role) {
        return ResponseEntity.ok(adminService.addRoleToUser(id, role));
    }

    @DeleteMapping("/users/{id}/role/{role}")
    public ResponseEntity<UserResponse> removeRoleFromUser(
            @PathVariable String id, 
            @PathVariable String role) {
        return ResponseEntity.ok(adminService.removeRoleFromUser(id, role));
    }

    @PutMapping("/users/{id}/disable")
    public ResponseEntity<UserResponse> disableUser(@PathVariable String id) {
        return ResponseEntity.ok(adminService.disableUser(id));
    }

    @PutMapping("/users/{id}/enable")
    public ResponseEntity<UserResponse> enableUser(@PathVariable String id) {
        return ResponseEntity.ok(adminService.enableUser(id));
    }
}