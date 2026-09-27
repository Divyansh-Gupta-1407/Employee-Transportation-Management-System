package com.ets.repository;

import com.ets.entity.TripPassenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripPassengerRepository extends JpaRepository<TripPassenger, Long> {
    List<TripPassenger> findByTripId(Long tripId);
    boolean existsByTripIdAndEmployeeId(Long tripId, Long employeeId);
    void deleteByTripIdAndEmployeeId(Long tripId, Long employeeId);
    long countByTripId(Long tripId);
}
