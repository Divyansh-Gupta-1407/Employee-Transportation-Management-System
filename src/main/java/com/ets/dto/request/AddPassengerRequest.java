package com.ets.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddPassengerRequest {
    @NotNull
    private Long employeeId;

    private Long pickupStopId;
    private Long dropoffStopId;
}
