package com.booktable.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private boolean isAuthenticated;
    private String currentRole; // Added for frontend role management
}