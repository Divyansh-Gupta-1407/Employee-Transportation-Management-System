package com.ets.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class CreateRouteRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String source;

    @NotBlank
    private String destination;

    private Double distanceKm;

    private Integer estimatedDurationMins;

    private List<RouteStopRequest> stops;
}
