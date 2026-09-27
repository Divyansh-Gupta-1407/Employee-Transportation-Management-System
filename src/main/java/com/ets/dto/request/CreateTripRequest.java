package com.ets.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateTripRequest {
    @NotNull
    private Long routeId;

    @NotNull
    private Long vehicleId;

    @NotNull
    private Long driverId;

    @NotNull
    @Future
    private LocalDateTime scheduledDeparture;

    @NotNull
    @Future
    private LocalDateTime scheduledArrival;

    private String notes;
}
