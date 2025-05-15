package com.booktable.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Table {
    
    private String id;
    private String name;
    private int capacity;
    private boolean isAvailable;
}