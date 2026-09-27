package com.ets.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RouteStopRequest {
    @NotBlank
    private String stopName;

    @NotNull
    @Min(1)
    private Integer stopOrder;

    private Double latitude;
    private Double longitude;
}
