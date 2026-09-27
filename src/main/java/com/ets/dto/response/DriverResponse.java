package com.ets.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String licenseNumber;
    private LocalDate licenseExpiry;
    private Boolean available;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
