package com.ets.service.impl;

import com.ets.dto.response.DashboardResponse;
import com.ets.dto.response.EmployeeResponse;
import com.ets.dto.response.TripReportResponse;
import com.ets.entity.Employee;
import com.ets.enums.TripStatus;
import com.ets.exception.ResourceNotFoundException;
import com.ets.mapper.EmployeeMapper;
import com.ets.repository.*;
import com.ets.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AdminServiceImpl implements AdminService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final VehicleRepository vehicleRepository;
    private final RouteRepository routeRepository;
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;

    @Override
    public DashboardResponse getDashboard() {
        return DashboardResponse.builder()
                .totalEmployees(employeeRepository.count())
                .activeEmployees(employeeRepository.countByActiveTrue())
                .totalDepartments(departmentRepository.count())
                .totalVehicles(vehicleRepository.count())
                .activeVehicles(vehicleRepository.countByActiveTrue())
                .totalRoutes(routeRepository.count())
                .activeRoutes(routeRepository.countByActiveTrue())
                .totalDrivers(driverRepository.count())
                .availableDrivers(driverRepository.countByAvailableTrue())
                .totalTrips(tripRepository.count())
                .scheduledTrips(tripRepository.countByStatus(TripStatus.SCHEDULED))
                .completedTrips(tripRepository.countByStatus(TripStatus.COMPLETED))
                .build();
    }

    @Override
    public TripReportResponse getTripReport(LocalDateTime from, LocalDateTime to) {
        long totalTrips = tripRepository.countByScheduledDepartureBetween(from, to);
        long completedTrips = tripRepository.countByStatusAndScheduledDepartureBetween(TripStatus.COMPLETED, from, to);
        long cancelledTrips = tripRepository.countByStatusAndScheduledDepartureBetween(TripStatus.CANCELLED, from, to);

        return TripReportResponse.builder()
                .totalTrips(totalTrips)
                .completedTrips(completedTrips)
                .cancelledTrips(cancelledTrips)
                .averagePassengersPerTrip(totalTrips > 0 ? (double) completedTrips / totalTrips * 100 : 0.0)
                .mostUsedRoute("N/A")
                .busiestDay("N/A")
                .build();
    }

    @Override
    @Transactional
    public EmployeeResponse toggleEmployeeStatus(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", employeeId));

        employee.setActive(!employee.isActive());
        Employee savedEmployee = employeeRepository.save(employee);
        log.info("Toggled employee status for id={}, active={}", employeeId, savedEmployee.isActive());
        return EmployeeMapper.toResponse(savedEmployee);
    }
}
