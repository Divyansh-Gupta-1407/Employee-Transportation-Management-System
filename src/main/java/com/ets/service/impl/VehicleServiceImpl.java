package com.ets.service.impl;

import com.ets.dto.request.CreateVehicleRequest;
import com.ets.dto.request.UpdateVehicleRequest;
import com.ets.dto.PagedResponse;
import com.ets.dto.response.VehicleResponse;
import com.ets.exception.DuplicateResourceException;
import com.ets.exception.ResourceNotFoundException;
import com.ets.mapper.VehicleMapper;
import com.ets.entity.Vehicle;
import com.ets.repository.VehicleRepository;
import com.ets.service.VehicleService;
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
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<VehicleResponse> getAllVehicles(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);
        return PagedResponse.from(vehicles.map(VehicleMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        return VehicleMapper.toResponse(vehicle);
    }

    @Override
    @Transactional
    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        if (vehicleRepository.existsByRegistrationNumber(request.getRegistrationNumber())) {
            throw new DuplicateResourceException("Vehicle", "registrationNumber", request.getRegistrationNumber());
        }

        Vehicle vehicle = new Vehicle();
        vehicle.setRegistrationNumber(request.getRegistrationNumber());
        vehicle.setModel(request.getModel());
        vehicle.setCapacity(request.getCapacity());
        vehicle.setType(request.getType());
        vehicle.setActive(true);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Created new vehicle with id: {}", savedVehicle.getId());
        return VehicleMapper.toResponse(savedVehicle);
    }

    @Override
    @Transactional
    public VehicleResponse updateVehicle(Long id, UpdateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));

        if (request.getModel() != null) {
            vehicle.setModel(request.getModel());
        }
        if (request.getCapacity() != null) {
            vehicle.setCapacity(request.getCapacity());
        }
        if (request.getType() != null) {
            vehicle.setType(request.getType());
        }
        if (request.getActive() != null) {
            vehicle.setActive(request.getActive());
        }

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        log.info("Updated vehicle with id: {}", id);
        return VehicleMapper.toResponse(updatedVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle", "id", id));
        vehicle.setActive(false);
        vehicleRepository.save(vehicle);
        log.info("Soft deleted vehicle with id: {}", id);
    }
}
