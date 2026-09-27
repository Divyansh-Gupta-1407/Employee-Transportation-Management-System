package com.ets.service.impl;

import com.ets.dto.request.AddPassengerRequest;
import com.ets.dto.request.CreateTripRequest;
import com.ets.dto.request.UpdateTripStatusRequest;
import com.ets.dto.PagedResponse;
import com.ets.dto.response.TripResponse;
import com.ets.exception.BadRequestException;
import com.ets.exception.DuplicateResourceException;
import com.ets.exception.ResourceNotFoundException;
import com.ets.mapper.TripMapper;
import com.ets.entity.*;
import com.ets.enums.BookingStatus;
import com.ets.enums.TripStatus;
import com.ets.repository.*;
import com.ets.service.TripService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final RouteRepository routeRepository;
    private final VehicleRepository vehicleRepository;
    private final DriverRepository driverRepository;
    private final EmployeeRepository employeeRepository;
    private final TripPassengerRepository tripPassengerRepository;
    private final RouteStopRepository routeStopRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TripResponse> getAllTrips(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Trip> trips = tripRepository.findAll(pageable);
        return PagedResponse.from(trips.map(TripMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public TripResponse getTripById(Long id) {
        Trip trip = tripRepository.findByIdWithPassengers(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));
        return TripMapper.toResponse(trip);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TripResponse> getTripsByStatus(TripStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledDeparture").ascending());
        Page<Trip> trips = tripRepository.findByStatus(status, pageable);
        return PagedResponse.from(trips.map(TripMapper::toResponse));
    }

    @Override
    @Transactional
    public TripResponse createTrip(CreateTripRequest request) {
        Route route = routeRepository.findById(request.getRouteId())
                .orElseThrow(() -> new ResourceNotFoundException("Route", "id", request.getRouteId()));
        if (!route.isActive()) {
            throw new BadRequestException("Cannot create trip for inactive route.");
        }

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", request.getVehicleId()));
        if (!vehicle.isActive()) {
            throw new BadRequestException("Cannot create trip with inactive vehicle.");
        }

        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", request.getDriverId()));
        if (!driver.isAvailable()) {
            throw new BadRequestException("Cannot create trip with unavailable driver.");
        }

        if (!request.getScheduledArrival().isAfter(request.getScheduledDeparture())) {
            throw new BadRequestException("Scheduled arrival must be after scheduled departure.");
        }

        Trip trip = new Trip();
        trip.setRoute(route);
        trip.setVehicle(vehicle);
        trip.setDriver(driver);
        trip.setScheduledDeparture(request.getScheduledDeparture());
        trip.setScheduledArrival(request.getScheduledArrival());
        trip.setStatus(TripStatus.SCHEDULED);
        trip.setNotes(request.getNotes());

        Trip savedTrip = tripRepository.save(trip);
        log.info("Created new trip with id: {}", savedTrip.getId());
        return TripMapper.toResponse(savedTrip);
    }

    @Override
    @Transactional
    public TripResponse updateTripStatus(Long id, UpdateTripStatusRequest request) {
        Trip trip = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", id));

        TripStatus currentStatus = trip.getStatus();
        TripStatus newStatus = request.getStatus();

        if (newStatus == TripStatus.IN_PROGRESS) {
            if (currentStatus != TripStatus.SCHEDULED) {
                throw new BadRequestException("Can only transition to IN_PROGRESS from SCHEDULED.");
            }
            trip.setActualDeparture(LocalDateTime.now());
        } else if (newStatus == TripStatus.COMPLETED) {
            if (currentStatus != TripStatus.IN_PROGRESS) {
                throw new BadRequestException("Can only transition to COMPLETED from IN_PROGRESS.");
            }
            trip.setActualArrival(LocalDateTime.now());
        } else if (newStatus == TripStatus.CANCELLED) {
            if (currentStatus == TripStatus.COMPLETED) {
                throw new BadRequestException("Cannot cancel a completed trip.");
            }
        }

        trip.setStatus(newStatus);
        Trip updatedTrip = tripRepository.save(trip);
        log.info("Updated trip {} status to {}", id, newStatus);
        return TripMapper.toResponse(updatedTrip);
    }

    @Override
    @Transactional
    public void addPassenger(Long tripId, AddPassengerRequest request) {
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException("Trip", "id", tripId));

        if (trip.getStatus() != TripStatus.SCHEDULED) {
            throw new BadRequestException("Can only add passengers to scheduled trips.");
        }

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));

        if (tripPassengerRepository.existsByTripIdAndEmployeeId(tripId, request.getEmployeeId())) {
            throw new DuplicateResourceException("TripPassenger", "employeeId", request.getEmployeeId());
        }

        long currentPassengers = tripPassengerRepository.countByTripId(tripId);
        if (currentPassengers >= trip.getVehicle().getCapacity()) {
            throw new BadRequestException("Vehicle capacity reached for this trip.");
        }

        RouteStop pickupStop = null;
        if (request.getPickupStopId() != null) {
            pickupStop = routeStopRepository.findById(request.getPickupStopId())
                    .orElseThrow(() -> new ResourceNotFoundException("RouteStop", "id", request.getPickupStopId()));
        }

        RouteStop dropoffStop = null;
        if (request.getDropoffStopId() != null) {
            dropoffStop = routeStopRepository.findById(request.getDropoffStopId())
                    .orElseThrow(() -> new ResourceNotFoundException("RouteStop", "id", request.getDropoffStopId()));
        }

        TripPassenger passenger = new TripPassenger();
        passenger.setTrip(trip);
        passenger.setEmployee(employee);
        passenger.setPickupStop(pickupStop);
        passenger.setDropoffStop(dropoffStop);
        passenger.setBookingStatus(BookingStatus.CONFIRMED);
        passenger.setBookedAt(LocalDateTime.now());

        tripPassengerRepository.save(passenger);
        log.info("Added passenger {} to trip {}", request.getEmployeeId(), tripId);
    }

    @Override
    @Transactional
    public void removePassenger(Long tripId, Long passengerId) {
        TripPassenger passenger = tripPassengerRepository.findById(passengerId)
                .orElseThrow(() -> new ResourceNotFoundException("TripPassenger", "id", passengerId));
        
        if (!passenger.getTrip().getId().equals(tripId)) {
            throw new BadRequestException("Passenger does not belong to this trip.");
        }

        passenger.setBookingStatus(BookingStatus.CANCELLED);
        tripPassengerRepository.save(passenger);
        log.info("Removed/Cancelled passenger {} from trip {}", passengerId, tripId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<TripResponse> getTripsByRoute(Long routeId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("scheduledDeparture").ascending());
        Page<Trip> trips = tripRepository.findByRouteId(routeId, pageable);
        return PagedResponse.from(trips.map(TripMapper::toResponse));
    }
}
