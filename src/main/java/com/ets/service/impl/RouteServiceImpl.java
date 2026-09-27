package com.ets.service.impl;

import com.ets.dto.request.CreateRouteRequest;
import com.ets.dto.request.RouteStopRequest;
import com.ets.dto.request.UpdateRouteRequest;
import com.ets.dto.PagedResponse;
import com.ets.dto.response.RouteResponse;
import com.ets.exception.ResourceNotFoundException;
import com.ets.mapper.RouteMapper;
import com.ets.entity.Route;
import com.ets.entity.RouteStop;
import com.ets.repository.RouteRepository;
import com.ets.service.RouteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;

    @Override
    @Transactional(readOnly = true)
    @Cacheable("routes")
    public PagedResponse<RouteResponse> getAllRoutes(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Route> routes = routeRepository.findAll(pageable);
        return PagedResponse.from(routes.map(RouteMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public RouteResponse getRouteById(Long id) {
        Route route = routeRepository.findByIdWithStops(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        return RouteMapper.toResponse(route);
    }

    @Override
    @Transactional
    @CacheEvict(value = "routes", allEntries = true)
    public RouteResponse createRoute(CreateRouteRequest request) {
        Route route = new Route();
        route.setName(request.getName());
        route.setSource(request.getSource());
        route.setDestination(request.getDestination());
        route.setDistanceKm(request.getDistanceKm());
        route.setEstimatedDurationMins(request.getEstimatedDurationMins());
        route.setActive(true);

        if (request.getStops() != null) {
            for (RouteStopRequest stopReq : request.getStops()) {
                RouteStop stop = new RouteStop();
                stop.setStopName(stopReq.getStopName());
                stop.setStopOrder(stopReq.getStopOrder());
                stop.setLatitude(stopReq.getLatitude());
                stop.setLongitude(stopReq.getLongitude());
                route.addStop(stop);
            }
        }

        Route savedRoute = routeRepository.save(route);
        log.info("Created new route with id: {}", savedRoute.getId());
        return RouteMapper.toResponse(savedRoute);
    }

    @Override
    @Transactional
    @CacheEvict(value = "routes", allEntries = true)
    public RouteResponse updateRoute(Long id, UpdateRouteRequest request) {
        Route route = routeRepository.findByIdWithStops(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));

        if (request.getName() != null) {
            route.setName(request.getName());
        }
        if (request.getSource() != null) {
            route.setSource(request.getSource());
        }
        if (request.getDestination() != null) {
            route.setDestination(request.getDestination());
        }
        if (request.getDistanceKm() != null) {
            route.setDistanceKm(request.getDistanceKm());
        }
        if (request.getEstimatedDurationMins() != null) {
            route.setEstimatedDurationMins(request.getEstimatedDurationMins());
        }
        if (request.getActive() != null) {
            route.setActive(request.getActive());
        }

        if (request.getStops() != null) {
            route.getStops().clear();
            for (RouteStopRequest stopReq : request.getStops()) {
                RouteStop stop = new RouteStop();
                stop.setStopName(stopReq.getStopName());
                stop.setStopOrder(stopReq.getStopOrder());
                stop.setLatitude(stopReq.getLatitude());
                stop.setLongitude(stopReq.getLongitude());
                route.addStop(stop);
            }
        }

        Route updatedRoute = routeRepository.save(route);
        log.info("Updated route with id: {}", id);
        return RouteMapper.toResponse(updatedRoute);
    }

    @Override
    @Transactional
    @CacheEvict(value = "routes", allEntries = true)
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", id));
        route.setActive(false);
        routeRepository.save(route);
        log.info("Soft deleted route with id: {}", id);
    }
}
