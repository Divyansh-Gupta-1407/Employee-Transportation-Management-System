package com.ets.controller;

import com.ets.constants.AppConstants;
import com.ets.dto.request.AddPassengerRequest;
import com.ets.dto.request.CreateTripRequest;
import com.ets.dto.request.UpdateTripStatusRequest;
import com.ets.dto.PagedResponse;
import com.ets.dto.response.TripResponse;
import com.ets.enums.TripStatus;
import com.ets.service.TripService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping
    public ResponseEntity<PagedResponse<TripResponse>> getAllTrips(
            @RequestParam(value = "status", required = false) TripStatus status,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size,
            @RequestParam(value = "sortBy", defaultValue = AppConstants.DEFAULT_SORT_BY, required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = AppConstants.DEFAULT_SORT_DIRECTION, required = false) String sortDir
    ) {
        if (status != null) {
            return ResponseEntity.ok(tripService.getTripsByStatus(status, page, size));
        }
        return ResponseEntity.ok(tripService.getAllTrips(page, size, sortBy, sortDir));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTripById(@PathVariable Long id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest request) {
        return new ResponseEntity<>(tripService.createTrip(request), HttpStatus.CREATED);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<TripResponse> updateTripStatus(@PathVariable Long id, @Valid @RequestBody UpdateTripStatusRequest request) {
        return ResponseEntity.ok(tripService.updateTripStatus(id, request));
    }

    @PostMapping("/{tripId}/passengers")
    public ResponseEntity<Void> addPassenger(@PathVariable Long tripId, @Valid @RequestBody AddPassengerRequest request) {
        tripService.addPassenger(tripId, request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @DeleteMapping("/{tripId}/passengers/{passengerId}")
    public ResponseEntity<Void> removePassenger(@PathVariable Long tripId, @PathVariable Long passengerId) {
        tripService.removePassenger(tripId, passengerId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/route/{routeId}")
    public ResponseEntity<PagedResponse<TripResponse>> getTripsByRoute(
            @PathVariable Long routeId,
            @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int page,
            @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int size
    ) {
        return ResponseEntity.ok(tripService.getTripsByRoute(routeId, page, size));
    }
}
