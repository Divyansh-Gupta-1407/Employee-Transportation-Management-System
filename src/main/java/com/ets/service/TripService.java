package com.ets.service;

import com.ets.dto.request.AddPassengerRequest;
import com.ets.dto.request.CreateTripRequest;
import com.ets.dto.request.UpdateTripStatusRequest;
import com.ets.dto.PagedResponse;
import com.ets.dto.response.TripResponse;
import com.ets.enums.TripStatus;

public interface TripService {
    PagedResponse<TripResponse> getAllTrips(int page, int size, String sortBy, String sortDir);
    TripResponse getTripById(Long id);
    PagedResponse<TripResponse> getTripsByStatus(TripStatus status, int page, int size);
    TripResponse createTrip(CreateTripRequest request);
    TripResponse updateTripStatus(Long id, UpdateTripStatusRequest request);
    void addPassenger(Long tripId, AddPassengerRequest request);
    void removePassenger(Long tripId, Long passengerId);
    PagedResponse<TripResponse> getTripsByRoute(Long routeId, int page, int size);
}
