package com.ets.repository;

import com.ets.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmployeeCode(String code);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmployeeCode(String code);

    boolean existsByEmail(String email);

    Page<Employee> findByDepartmentId(Long departmentId, Pageable pageable);

    @Query("SELECT e FROM Employee e WHERE LOWER(e.firstName) LIKE LOWER(CONCAT('%',:query,'%')) OR LOWER(e.lastName) LIKE LOWER(CONCAT('%',:query,'%')) OR LOWER(e.email) LIKE LOWER(CONCAT('%',:query,'%')) OR LOWER(e.employeeCode) LIKE LOWER(CONCAT('%',:query,'%'))")
    Page<Employee> search(@Param("query") String query, Pageable pageable);

    Page<Employee> findByActiveTrue(Pageable pageable);

    Page<Employee> findByDepartmentIdAndActiveTrue(Long departmentId, Pageable pageable);

    long countByActiveTrue();
}
