package com.ets.dto.request;

import com.ets.enums.VehicleType;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateVehicleRequest {
    private String model;

    @Min(1)
    private Integer capacity;

    private VehicleType type;

    private Boolean active;
}
