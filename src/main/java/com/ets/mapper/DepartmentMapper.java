package com.ets.mapper;

import com.ets.dto.response.DepartmentResponse;
import com.ets.entity.Department;

import java.util.List;
import java.util.stream.Collectors;

public final class DepartmentMapper {

    private DepartmentMapper() {
        // Utility class
    }

    public static DepartmentResponse toResponse(Department entity) {
        if (entity == null) {
            return null;
        }

        return DepartmentResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .code(entity.getCode())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static List<DepartmentResponse> toResponseList(List<Department> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(DepartmentMapper::toResponse)
                .collect(Collectors.toList());
    }
}
