package com.ets.repository;

import com.ets.entity.Vehicle;
import com.ets.enums.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByRegistrationNumber(String registrationNumber);
    boolean existsByRegistrationNumber(String registrationNumber);
    Page<Vehicle> findByActiveTrue(Pageable pageable);
    Page<Vehicle> findByType(VehicleType type, Pageable pageable);
    long countByActiveTrue();
}
