package com.ets.repository;

import com.ets.entity.Route;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Page<Route> findByActiveTrue(Pageable pageable);
    long countByActiveTrue();

    @Query("SELECT r FROM Route r LEFT JOIN FETCH r.stops WHERE r.id = :id")
    Optional<Route> findByIdWithStops(@Param("id") Long id);
}
