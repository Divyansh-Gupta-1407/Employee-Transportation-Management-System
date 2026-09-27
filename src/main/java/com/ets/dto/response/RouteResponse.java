package com.ets.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RouteResponse {
    private Long id;
    private String name;
    private String source;
    private String destination;
    private Double distanceKm;
    private Integer estimatedDurationMins;
    private Boolean active;
    private List<RouteStopResponse> stops;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
