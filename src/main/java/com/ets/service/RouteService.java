package com.ets.service;

import com.ets.dto.request.CreateRouteRequest;
import com.ets.dto.request.UpdateRouteRequest;
import com.ets.dto.PagedResponse;
import com.ets.dto.response.RouteResponse;

public interface RouteService {
    PagedResponse<RouteResponse> getAllRoutes(int page, int size, String sortBy, String sortDir);
    RouteResponse getRouteById(Long id);
    RouteResponse createRoute(CreateRouteRequest request);
    RouteResponse updateRoute(Long id, UpdateRouteRequest request);
    void deleteRoute(Long id);
}
