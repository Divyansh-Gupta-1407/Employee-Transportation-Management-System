package com.ets.mapper;

import com.ets.dto.response.VehicleResponse;
import com.ets.entity.Vehicle;

import java.util.List;
import java.util.stream.Collectors;

public class VehicleMapper {

    private VehicleMapper() {
        // Utility class
    }

    public static VehicleResponse toResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .registrationNumber(vehicle.getRegistrationNumber())
                .model(vehicle.getModel())
                .capacity(vehicle.getCapacity())
                .type(vehicle.getType())
                .active(vehicle.isActive())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }

    public static List<VehicleResponse> toResponseList(List<Vehicle> vehicles) {
        if (vehicles == null) {
            return null;
        }
        return vehicles.stream()
                .map(VehicleMapper::toResponse)
                .collect(Collectors.toList());
    }
}
