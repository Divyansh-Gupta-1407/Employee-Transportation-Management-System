package com.ets.service.impl;

import com.ets.dto.request.RegisterDriverRequest;
import com.ets.dto.response.DriverResponse;
import com.ets.dto.PagedResponse;
import com.ets.exception.DuplicateResourceException;
import com.ets.exception.ResourceNotFoundException;
import com.ets.mapper.DriverMapper;
import com.ets.entity.Driver;
import com.ets.entity.Employee;
import com.ets.repository.DriverRepository;
import com.ets.repository.EmployeeRepository;
import com.ets.service.DriverService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DriverResponse> getAllDrivers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Driver> drivers = driverRepository.findAll(pageable);
        return PagedResponse.from(drivers.map(DriverMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public DriverResponse getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        return DriverMapper.toResponse(driver);
    }

    @Override
    @Transactional
    public DriverResponse registerDriver(RegisterDriverRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", request.getEmployeeId()));

        if (driverRepository.existsByEmployeeId(request.getEmployeeId())) {
            throw new DuplicateResourceException("Driver", "employeeId", request.getEmployeeId());
        }

        if (driverRepository.existsByLicenseNumber(request.getLicenseNumber())) {
            throw new DuplicateResourceException("Driver", "licenseNumber", request.getLicenseNumber());
        }

        Driver driver = new Driver();
        driver.setEmployee(employee);
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setLicenseExpiry(request.getLicenseExpiry());
        driver.setAvailable(true);

        Driver savedDriver = driverRepository.save(driver);
        log.info("Registered new driver with id: {}", savedDriver.getId());
        return DriverMapper.toResponse(savedDriver);
    }

    @Override
    @Transactional
    public void updateAvailability(Long id, boolean available) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id));
        driver.setAvailable(available);
        driverRepository.save(driver);
        log.info("Updated availability for driver id: {} to {}", id, available);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<DriverResponse> getAvailableDrivers(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Driver> drivers = driverRepository.findByAvailableTrue(pageable);
        return PagedResponse.from(drivers.map(DriverMapper::toResponse));
    }
}
