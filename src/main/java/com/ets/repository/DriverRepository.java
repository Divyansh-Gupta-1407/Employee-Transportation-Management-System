package com.ets.repository;

import com.ets.entity.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    Optional<Driver> findByEmployeeId(Long employeeId);
    boolean existsByEmployeeId(Long employeeId);
    boolean existsByLicenseNumber(String licenseNumber);
    Page<Driver> findByAvailableTrue(Pageable pageable);
    long countByAvailableTrue();
}
