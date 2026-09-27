package com.ets.mapper;

import com.ets.dto.response.EmployeeResponse;
import com.ets.entity.Employee;

import java.util.List;
import java.util.stream.Collectors;

public final class EmployeeMapper {

    private EmployeeMapper() {
        // Utility class
    }

    public static EmployeeResponse toResponse(Employee entity) {
        if (entity == null) {
            return null;
        }

        EmployeeResponse.EmployeeResponseBuilder builder = EmployeeResponse.builder()
                .id(entity.getId())
                .employeeCode(entity.getEmployeeCode())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .designation(entity.getDesignation())
                .address(entity.getAddress())
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        if (entity.getDepartment() != null) {
            builder.departmentId(entity.getDepartment().getId())
                   .departmentName(entity.getDepartment().getName());
        }

        return builder.build();
    }

    public static List<EmployeeResponse> toResponseList(List<Employee> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(EmployeeMapper::toResponse)
                .collect(Collectors.toList());
    }
}
