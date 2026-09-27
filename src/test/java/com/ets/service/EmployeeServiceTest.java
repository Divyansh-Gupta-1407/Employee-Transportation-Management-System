package com.ets.service;

import com.ets.dto.PagedResponse;
import com.ets.dto.request.CreateEmployeeRequest;
import com.ets.dto.request.UpdateEmployeeRequest;
import com.ets.dto.response.EmployeeResponse;
import com.ets.entity.Department;
import com.ets.entity.Employee;
import com.ets.exception.DuplicateResourceException;
import com.ets.exception.ResourceNotFoundException;
import com.ets.repository.DepartmentRepository;
import com.ets.repository.EmployeeRepository;
import com.ets.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Test
    void createEmployee_success() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setEmail("test@test.com");
        request.setDepartmentId(1L);

        when(employeeRepository.existsByEmail("test@test.com")).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(new Department()));
        when(employeeRepository.count()).thenReturn(0L);

        Employee saved = new Employee();
        saved.setId(1L);
        saved.setEmail("test@test.com");
        saved.setEmployeeCode("EMP-00001");
        
        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeResponse response = employeeService.createEmployee(request);

        assertNotNull(response);
        assertEquals("EMP-00001", response.getEmployeeCode());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_duplicateEmail_throwsException() {
        CreateEmployeeRequest request = new CreateEmployeeRequest();
        request.setEmail("test@test.com");

        when(employeeRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> employeeService.createEmployee(request));
    }

    @Test
    void getEmployeeById_found() {
        Employee emp = new Employee();
        emp.setId(1L);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        EmployeeResponse res = employeeService.getEmployeeById(1L);
        assertNotNull(res);
        assertEquals(1L, res.getId());
    }

    @Test
    void getEmployeeById_notFound_throwsException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    void getAllEmployees_returnsPaged() {
        Page<Employee> paged = new PageImpl<>(List.of(new Employee()));
        when(employeeRepository.findAll(any(Pageable.class))).thenReturn(paged);

        PagedResponse<EmployeeResponse> res = employeeService.getAllEmployees(0, 10, "id", "asc");
        assertNotNull(res);
        assertEquals(1, res.getContent().size());
    }

    @Test
    void updateEmployee_success() {
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setEmail("old@test.com");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        UpdateEmployeeRequest req = new UpdateEmployeeRequest();
        req.setFirstName("New");

        when(employeeRepository.save(any(Employee.class))).thenReturn(emp);

        EmployeeResponse res = employeeService.updateEmployee(1L, req);
        assertNotNull(res);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void deleteEmployee_setsInactive() {
        Employee emp = new Employee();
        emp.setId(1L);
        emp.setActive(true);
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(emp));

        employeeService.deleteEmployee(1L);

        assertFalse(emp.isActive());
        verify(employeeRepository).save(emp);
    }
}
