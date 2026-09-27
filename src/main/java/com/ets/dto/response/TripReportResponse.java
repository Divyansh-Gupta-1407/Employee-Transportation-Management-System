package com.ets.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripReportResponse {
    private long totalTrips;
    private long completedTrips;
    private long cancelledTrips;
    private double averagePassengersPerTrip;
    private String mostUsedRoute;
    private String busiestDay;
}
