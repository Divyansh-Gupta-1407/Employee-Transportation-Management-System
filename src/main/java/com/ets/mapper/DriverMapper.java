package com.ets.mapper;

import com.ets.dto.response.DriverResponse;
import com.ets.entity.Driver;

import java.util.List;
import java.util.stream.Collectors;

public class DriverMapper {

    private DriverMapper() {
        // Utility class
    }

    public static DriverResponse toResponse(Driver driver) {
        if (driver == null) {
            return null;
        }

        String employeeName = null;
        Long employeeId = null;
        if (driver.getEmployee() != null) {
            employeeId = driver.getEmployee().getId();
            employeeName = driver.getEmployee().getFirstName() + " " + driver.getEmployee().getLastName();
        }

        return DriverResponse.builder()
                .id(driver.getId())
                .employeeId(employeeId)
                .employeeName(employeeName)
                .licenseNumber(driver.getLicenseNumber())
                .licenseExpiry(driver.getLicenseExpiry())
                .available(driver.isAvailable())
                .createdAt(driver.getCreatedAt())
                .updatedAt(driver.getUpdatedAt())
                .build();
    }

    public static List<DriverResponse> toResponseList(List<Driver> drivers) {
        if (drivers == null) {
            return null;
        }
        return drivers.stream()
                .map(DriverMapper::toResponse)
                .collect(Collectors.toList());
    }
}
