package com.ets.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateRouteRequest {
    private String name;
    private String source;
    private String destination;
    private Double distanceKm;
    private Integer estimatedDurationMins;
    private Boolean active;
    private List<RouteStopRequest> stops;
}
