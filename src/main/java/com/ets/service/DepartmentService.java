package com.ets.service;

import com.ets.dto.request.CreateDepartmentRequest;
import com.ets.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {
    List<DepartmentResponse> getAllDepartments();
    DepartmentResponse getDepartmentById(Long id);
    DepartmentResponse createDepartment(CreateDepartmentRequest request);
    DepartmentResponse updateDepartment(Long id, CreateDepartmentRequest request);
    void deleteDepartment(Long id);
}
