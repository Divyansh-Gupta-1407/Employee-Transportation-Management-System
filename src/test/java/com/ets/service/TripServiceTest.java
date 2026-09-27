package com.ets.service;

import com.ets.dto.request.AddPassengerRequest;
import com.ets.dto.request.CreateTripRequest;
import com.ets.dto.request.UpdateTripStatusRequest;
import com.ets.exception.BadRequestException;
import com.ets.exception.DuplicateResourceException;
import com.ets.exception.ResourceNotFoundException;
import com.ets.entity.*;
import com.ets.enums.TripStatus;
import com.ets.repository.*;
import com.ets.service.impl.TripServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;
    @Mock
    private RouteRepository routeRepository;
    @Mock
    private VehicleRepository vehicleRepository;
    @Mock
    private DriverRepository driverRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private TripPassengerRepository tripPassengerRepository;
    @Mock
    private RouteStopRepository routeStopRepository;

    @InjectMocks
    private TripServiceImpl tripService;

    @Test
    void createTrip_success() {
        CreateTripRequest req = new CreateTripRequest();
        req.setRouteId(1L);
        req.setVehicleId(1L);
        req.setDriverId(1L);
        req.setScheduledDeparture(LocalDateTime.now().plusDays(1));
        req.setScheduledArrival(LocalDateTime.now().plusDays(1).plusHours(2));

        Route route = new Route();
        route.setId(1L);
        route.setActive(true);

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setActive(true);

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setAvailable(true);

        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));
        
        Trip savedTrip = new Trip();
        savedTrip.setId(10L);
        savedTrip.setRoute(route);
        savedTrip.setVehicle(vehicle);
        savedTrip.setDriver(driver);
        savedTrip.setStatus(TripStatus.SCHEDULED);
        
        when(tripRepository.save(any(Trip.class))).thenReturn(savedTrip);

        var response = tripService.createTrip(req);
        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(TripStatus.SCHEDULED, response.getStatus());
    }

    @Test
    void createTrip_routeNotFound_throwsException() {
        CreateTripRequest req = new CreateTripRequest();
        req.setRouteId(99L);
        
        when(routeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> tripService.createTrip(req));
    }

    @Test
    void createTrip_vehicleInactive_throwsException() {
        CreateTripRequest req = new CreateTripRequest();
        req.setRouteId(1L);
        req.setVehicleId(1L);

        Route route = new Route();
        route.setId(1L);
        route.setActive(true);
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setActive(false);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThrows(BadRequestException.class, () -> tripService.createTrip(req));
    }

    @Test
    void createTrip_driverUnavailable_throwsException() {
        CreateTripRequest req = new CreateTripRequest();
        req.setRouteId(1L);
        req.setVehicleId(1L);
        req.setDriverId(1L);

        Route route = new Route();
        route.setId(1L);
        route.setActive(true);
        when(routeRepository.findById(1L)).thenReturn(Optional.of(route));

        Vehicle vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setActive(true);
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        Driver driver = new Driver();
        driver.setId(1L);
        driver.setAvailable(false);
        when(driverRepository.findById(1L)).thenReturn(Optional.of(driver));

        assertThrows(BadRequestException.class, () -> tripService.createTrip(req));
    }

    @Test
    void updateTripStatus_toInProgress_setsActualDeparture() {
        UpdateTripStatusRequest req = new UpdateTripStatusRequest();
        req.setStatus(TripStatus.IN_PROGRESS);

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.SCHEDULED);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        var res = tripService.updateTripStatus(1L, req);
        
        assertEquals(TripStatus.IN_PROGRESS, res.getStatus());
        assertNotNull(trip.getActualDeparture());
    }

    @Test
    void updateTripStatus_toCompleted_setsActualArrival() {
        UpdateTripStatusRequest req = new UpdateTripStatusRequest();
        req.setStatus(TripStatus.COMPLETED);

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.IN_PROGRESS);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        var res = tripService.updateTripStatus(1L, req);
        
        assertEquals(TripStatus.COMPLETED, res.getStatus());
        assertNotNull(trip.getActualArrival());
    }

    @Test
    void updateTripStatus_invalidTransition_throwsException() {
        UpdateTripStatusRequest req = new UpdateTripStatusRequest();
        req.setStatus(TripStatus.COMPLETED);

        Trip trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.SCHEDULED);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));

        assertThrows(BadRequestException.class, () -> tripService.updateTripStatus(1L, req));
    }

    @Test
    void addPassenger_success() {
        AddPassengerRequest req = new AddPassengerRequest();
        req.setEmployeeId(1L);
        
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.SCHEDULED);
        Vehicle vehicle = new Vehicle();
        vehicle.setCapacity(10);
        trip.setVehicle(vehicle);

        Employee emp = new Employee();
        emp.setId(1L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));
        when(tripPassengerRepository.existsByTripIdAndEmployeeId(1L, 1L)).thenReturn(false);
        when(tripPassengerRepository.countByTripId(1L)).thenReturn(0L);

        tripService.addPassenger(1L, req);

        verify(tripPassengerRepository).save(any(TripPassenger.class));
    }

    @Test
    void addPassenger_alreadyBooked_throwsException() {
        AddPassengerRequest req = new AddPassengerRequest();
        req.setEmployeeId(1L);
        
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.SCHEDULED);

        Employee emp = new Employee();
        emp.setId(1L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));
        when(tripPassengerRepository.existsByTripIdAndEmployeeId(1L, 1L)).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> tripService.addPassenger(1L, req));
    }

    @Test
    void addPassenger_vehicleFull_throwsException() {
        AddPassengerRequest req = new AddPassengerRequest();
        req.setEmployeeId(1L);
        
        Trip trip = new Trip();
        trip.setId(1L);
        trip.setStatus(TripStatus.SCHEDULED);
        Vehicle vehicle = new Vehicle();
        vehicle.setCapacity(1);
        trip.setVehicle(vehicle);

        Employee emp = new Employee();
        emp.setId(1L);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(trip));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));
        when(tripPassengerRepository.existsByTripIdAndEmployeeId(1L, 1L)).thenReturn(false);
        when(tripPassengerRepository.countByTripId(1L)).thenReturn(1L); // full

        assertThrows(BadRequestException.class, () -> tripService.addPassenger(1L, req));
    }
}
