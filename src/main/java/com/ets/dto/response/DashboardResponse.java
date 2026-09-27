package com.ets.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long totalEmployees;
    private long activeEmployees;
    private long totalDepartments;
    private long totalVehicles;
    private long activeVehicles;
    private long totalRoutes;
    private long activeRoutes;
    private long totalDrivers;
    private long availableDrivers;
    private long totalTrips;
    private long scheduledTrips;
    private long completedTrips;
}
