package com.ets.mapper;

import com.ets.dto.response.TripPassengerResponse;
import com.ets.dto.response.TripResponse;
import com.ets.entity.Trip;
import com.ets.entity.TripPassenger;

import java.util.List;
import java.util.stream.Collectors;

public class TripMapper {

    private TripMapper() {
        // Utility class
    }

    public static TripResponse toResponse(Trip trip) {
        if (trip == null) {
            return null;
        }

        Long routeId = trip.getRoute() != null ? trip.getRoute().getId() : null;
        String routeName = trip.getRoute() != null ? trip.getRoute().getName() : null;

        Long vehicleId = trip.getVehicle() != null ? trip.getVehicle().getId() : null;
        String vehicleRegistration = trip.getVehicle() != null ? trip.getVehicle().getRegistrationNumber() : null;

        Long driverId = trip.getDriver() != null ? trip.getDriver().getId() : null;
        String driverName = null;
        if (trip.getDriver() != null && trip.getDriver().getEmployee() != null) {
            driverName = trip.getDriver().getEmployee().getFirstName() + " " + trip.getDriver().getEmployee().getLastName();
        }

        List<TripPassengerResponse> passengerResponses = null;
        int passengerCount = 0;
        if (trip.getPassengers() != null) {
            passengerResponses = trip.getPassengers().stream()
                    .map(TripMapper::toPassengerResponse)
                    .collect(Collectors.toList());
            passengerCount = trip.getPassengers().size();
        }

        return TripResponse.builder()
                .id(trip.getId())
                .routeId(routeId)
                .routeName(routeName)
                .vehicleId(vehicleId)
                .vehicleRegistration(vehicleRegistration)
                .driverId(driverId)
                .driverName(driverName)
                .scheduledDeparture(trip.getScheduledDeparture())
                .scheduledArrival(trip.getScheduledArrival())
                .actualDeparture(trip.getActualDeparture())
                .actualArrival(trip.getActualArrival())
                .status(trip.getStatus())
                .notes(trip.getNotes())
                .passengerCount(passengerCount)
                .passengers(passengerResponses)
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .build();
    }

    public static List<TripResponse> toResponseList(List<Trip> trips) {
        if (trips == null) {
            return null;
        }
        return trips.stream()
                .map(TripMapper::toResponse)
                .collect(Collectors.toList());
    }

    public static TripPassengerResponse toPassengerResponse(TripPassenger passenger) {
        if (passenger == null) {
            return null;
        }

        String employeeName = null;
        Long employeeId = null;
        if (passenger.getEmployee() != null) {
            employeeId = passenger.getEmployee().getId();
            employeeName = passenger.getEmployee().getFirstName() + " " + passenger.getEmployee().getLastName();
        }

        String pickupStopName = passenger.getPickupStop() != null ? passenger.getPickupStop().getStopName() : null;
        String dropoffStopName = passenger.getDropoffStop() != null ? passenger.getDropoffStop().getStopName() : null;

        return TripPassengerResponse.builder()
                .id(passenger.getId())
                .employeeId(employeeId)
                .employeeName(employeeName)
                .pickupStopName(pickupStopName)
                .dropoffStopName(dropoffStopName)
                .bookingStatus(passenger.getBookingStatus())
                .bookedAt(passenger.getBookedAt())
                .build();
    }
}
