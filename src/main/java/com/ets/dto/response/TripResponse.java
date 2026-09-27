package com.ets.dto.response;

import com.ets.enums.TripStatus;
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
public class TripResponse {
    private Long id;
    private Long routeId;
    private String routeName;
    private Long vehicleId;
    private String vehicleRegistration;
    private Long driverId;
    private String driverName;
    private LocalDateTime scheduledDeparture;
    private LocalDateTime scheduledArrival;
    private LocalDateTime actualDeparture;
    private LocalDateTime actualArrival;
    private TripStatus status;
    private String notes;
    private int passengerCount;
    private List<TripPassengerResponse> passengers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
