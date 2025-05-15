package com.booktable.api.service;

import com.booktable.api.dto.AuthRequest;
import com.booktable.api.dto.AuthResponse;
import com.booktable.api.dto.RegisterRequest;
import com.booktable.api.exception.ResourceAlreadyExistsException;
import com.booktable.api.model.User;
import com.booktable.api.repository.UserRepository;
import com.booktable.api.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceAlreadyExistsException("Email already registered");
        }
        
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .roles(Collections.singletonList("ROLE_CUSTOMER"))
                .createdAt(new Date())
                .enabled(true)
                .build();
        
        User savedUser = userRepository.save(user);
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        return AuthResponse.builder()
                .token(jwt)
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .firstName(savedUser.getFirstName())
                .lastName(savedUser.getLastName())
                .roles(savedUser.getRoles())
                .isAuthenticated(true)
                .currentRole("ROLE_CUSTOMER")
                .build();
    }
    
    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update last login time
        user.setLastLogin(new Date());
        userRepository.save(user);
        
        // Set the current role to the highest privilege role
        String currentRole = user.getRoles().contains("ROLE_ADMIN") ? "ROLE_ADMIN" :
                           user.getRoles().contains("ROLE_RESTAURANT_MANAGER") ? "ROLE_RESTAURANT_MANAGER" :
                           "ROLE_CUSTOMER";
        
        return AuthResponse.builder()
                .token(jwt)
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .roles(user.getRoles())
                .isAuthenticated(true)
                .currentRole(currentRole)
                .build();
    }
}