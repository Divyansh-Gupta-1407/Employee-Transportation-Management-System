package com.ets.service;

import com.ets.dto.request.CreateVehicleRequest;
import com.ets.dto.request.UpdateVehicleRequest;
import com.ets.dto.PagedResponse;
import com.ets.dto.response.VehicleResponse;

public interface VehicleService {
    PagedResponse<VehicleResponse> getAllVehicles(int page, int size, String sortBy, String sortDir);
    VehicleResponse getVehicleById(Long id);
    VehicleResponse createVehicle(CreateVehicleRequest request);
    VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request);
    void deleteVehicle(Long id);
}
