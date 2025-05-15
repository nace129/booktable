package com.booktable.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpeningHours {
    
    private DayOfWeek dayOfWeek;
    private LocalTime openTime;
    private LocalTime closeTime;
}