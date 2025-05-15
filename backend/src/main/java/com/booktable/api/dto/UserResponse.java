package com.booktable.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private List<String> roles;
    private Date createdAt;
    private Date lastLogin;
    private boolean enabled;
}