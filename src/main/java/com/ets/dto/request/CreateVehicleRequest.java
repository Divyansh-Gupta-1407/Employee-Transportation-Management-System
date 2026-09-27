package com.ets.dto.request;

import com.ets.enums.VehicleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateVehicleRequest {
    @NotBlank
    @Size(max = 20)
    private String registrationNumber;

    @NotBlank
    private String model;

    @Min(1)
    private Integer capacity;

    @NotNull
    private VehicleType type;
}
