package com.ets.service;

import com.ets.dto.request.RegisterDriverRequest;
import com.ets.dto.response.DriverResponse;
import com.ets.dto.PagedResponse;

public interface DriverService {
    PagedResponse<DriverResponse> getAllDrivers(int page, int size, String sortBy, String sortDir);
    DriverResponse getDriverById(Long id);
    DriverResponse registerDriver(RegisterDriverRequest request);
    void updateAvailability(Long id, boolean available);
    PagedResponse<DriverResponse> getAvailableDrivers(int page, int size, String sortBy, String sortDir);
}
