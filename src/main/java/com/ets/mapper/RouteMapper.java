package com.ets.mapper;

import com.ets.dto.response.RouteResponse;
import com.ets.dto.response.RouteStopResponse;
import com.ets.entity.Route;
import com.ets.entity.RouteStop;

import java.util.List;
import java.util.stream.Collectors;

public class RouteMapper {

    private RouteMapper() {
        // Utility class
    }

    public static RouteResponse toResponse(Route route) {
        if (route == null) {
            return null;
        }

        List<RouteStopResponse> stopResponses = null;
        if (route.getStops() != null) {
            stopResponses = route.getStops().stream()
                    .map(RouteMapper::toStopResponse)
                    .collect(Collectors.toList());
        }

        return RouteResponse.builder()
                .id(route.getId())
                .name(route.getName())
                .source(route.getSource())
                .destination(route.getDestination())
                .distanceKm(route.getDistanceKm())
                .estimatedDurationMins(route.getEstimatedDurationMins())
                .active(route.isActive())
                .stops(stopResponses)
                .createdAt(route.getCreatedAt())
                .updatedAt(route.getUpdatedAt())
                .build();
    }

    public static List<RouteResponse> toResponseList(List<Route> routes) {
        if (routes == null) {
            return null;
        }
        return routes.stream()
                .map(RouteMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static RouteStopResponse toStopResponse(RouteStop stop) {
        if (stop == null) {
            return null;
        }
        return RouteStopResponse.builder()
                .id(stop.getId())
                .stopName(stop.getStopName())
                .stopOrder(stop.getStopOrder())
                .latitude(stop.getLatitude())
                .longitude(stop.getLongitude())
                .build();
    }
}
