package com.ets.service.impl;

import com.ets.dto.PagedResponse;
import com.ets.dto.request.CreateEmployeeRequest;
import com.ets.dto.request.UpdateEmployeeRequest;
import com.ets.dto.response.EmployeeResponse;
import com.ets.entity.Department;
import com.ets.entity.Employee;
import com.ets.exception.DuplicateResourceException;
import com.ets.exception.ResourceNotFoundException;
import com.ets.mapper.EmployeeMapper;
import com.ets.repository.DepartmentRepository;
import com.ets.repository.EmployeeRepository;
import com.ets.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public PagedResponse<EmployeeResponse> getAllEmployees(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Employee> employees = employeeRepository.findAll(pageable);
        return PagedResponse.from(employees.map(EmployeeMapper::toResponse));
    }

    @Override
    @Cacheable("employees")
    public EmployeeResponse getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        return EmployeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponse createEmployee(CreateEmployeeRequest request) {
        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee", "email", request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        String employeeCode = "EMP-" + String.format("%05d", employeeRepository.count() + 1);

        Employee employee = new Employee();
        employee.setEmployeeCode(employeeCode);
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());
        employee.setDepartment(department);
        employee.setDesignation(request.getDesignation());
        employee.setAddress(request.getAddress());
        employee.setActive(true);

        Employee savedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toResponse(savedEmployee);
    }

    @Override
    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public EmployeeResponse updateEmployee(Long id, UpdateEmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));

        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        
        if (request.getEmail() != null && !request.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("Employee", "email", request.getEmail());
            }
            employee.setEmail(request.getEmail());
        }
        
        if (request.getPhone() != null) employee.setPhone(request.getPhone());
        if (request.getDesignation() != null) employee.setDesignation(request.getDesignation());
        if (request.getAddress() != null) employee.setAddress(request.getAddress());
        if (request.getActive() != null) employee.setActive(request.getActive());

        if (request.getDepartmentId() != null && (employee.getDepartment() == null || !employee.getDepartment().getId().equals(request.getDepartmentId()))) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));
            employee.setDepartment(department);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return EmployeeMapper.toResponse(updatedEmployee);
    }

    @Override
    @Transactional
    @CacheEvict(value = "employees", allEntries = true)
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", id));
        employee.setActive(false);
        employeeRepository.save(employee);
    }

    @Override
    public PagedResponse<EmployeeResponse> searchEmployees(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees = employeeRepository.search(query, pageable);
        return PagedResponse.from(employees.map(EmployeeMapper::toResponse));
    }

    @Override
    public PagedResponse<EmployeeResponse> getEmployeesByDepartment(Long departmentId, int page, int size) {
        if (!departmentRepository.existsById(departmentId)) {
            throw new ResourceNotFoundException("Department", "id", departmentId);
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Employee> employees = employeeRepository.findByDepartmentId(departmentId, pageable);
        return PagedResponse.from(employees.map(EmployeeMapper::toResponse));
    }
}
