package com.ets.dto.request;

import com.ets.enums.TripStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTripStatusRequest {
    @NotNull
    private TripStatus status;
}
