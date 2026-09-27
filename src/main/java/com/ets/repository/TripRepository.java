package com.ets.repository;

import com.ets.entity.Trip;
import com.ets.enums.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    Page<Trip> findByStatus(TripStatus status, Pageable pageable);
    Page<Trip> findByRouteId(Long routeId, Pageable pageable);
    Page<Trip> findByDriverId(Long driverId, Pageable pageable);
    Page<Trip> findByScheduledDepartureBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    long countByStatus(TripStatus status);
    List<Trip> findByStatusIn(List<TripStatus> statuses);

    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.passengers WHERE t.id = :id")
    Optional<Trip> findByIdWithPassengers(@Param("id") Long id);

    long countByScheduledDepartureBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusAndScheduledDepartureBetween(TripStatus status, LocalDateTime start, LocalDateTime end);
}
