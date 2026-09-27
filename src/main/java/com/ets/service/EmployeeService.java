package com.ets.service;

import com.ets.dto.PagedResponse;
import com.ets.dto.request.CreateEmployeeRequest;
import com.ets.dto.request.UpdateEmployeeRequest;
import com.ets.dto.response.EmployeeResponse;

public interface EmployeeService {

    PagedResponse<EmployeeResponse> getAllEmployees(int page, int size, String sortBy, String sortDir);

    EmployeeResponse getEmployeeById(Long id);

    EmployeeResponse createEmployee(CreateEmployeeRequest request);

    EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request);

    void deleteEmployee(Long id);

    PagedResponse<EmployeeResponse> searchEmployees(String query, int page, int size);

    PagedResponse<EmployeeResponse> getEmployeesByDepartment(Long departmentId, int page, int size);
}
