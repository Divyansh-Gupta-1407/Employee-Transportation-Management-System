package com.ets.controller;

import com.ets.BaseIntegrationTest;
import com.ets.dto.request.CreateDepartmentRequest;
import com.ets.dto.request.CreateEmployeeRequest;
import com.ets.dto.request.LoginRequest;
import com.ets.dto.request.UpdateEmployeeRequest;
import com.ets.dto.response.AuthResponse;
import com.ets.dto.response.DepartmentResponse;
import com.ets.dto.response.EmployeeResponse;
import com.ets.entity.Role;
import com.ets.entity.User;
import com.ets.repository.DepartmentRepository;
import com.ets.repository.EmployeeRepository;
import com.ets.repository.RoleRepository;
import com.ets.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EmployeeControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setup() {
        employeeRepository.deleteAll();
        departmentRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role adminRole = Role.builder().name("ROLE_ADMIN").build();
        adminRole = roleRepository.save(adminRole);

        User adminUser = User.builder()
                .username("admin")
                .email("admin@example.com")
                .password(passwordEncoder.encode("password"))
                .firstName("Admin")
                .lastName("User")
                .roles(Set.of(adminRole))
                .build();
        userRepository.save(adminUser);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("admin");
        loginRequest.setPassword("password");
        ResponseEntity<AuthResponse> authRes = restTemplate.postForEntity(
                "/api/auth/login", loginRequest, AuthResponse.class);
        assertNotNull(authRes.getBody());
        adminToken = authRes.getBody().getAccessToken();
    }

    @Test
    void testEmployeeLifecycle() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.set("Content-Type", "application/json");

        // 1. Create department
        CreateDepartmentRequest depReq = new CreateDepartmentRequest();
        depReq.setName("IT Dept");
        depReq.setCode("IT");
        ResponseEntity<DepartmentResponse> depRes = restTemplate.exchange(
                "/api/departments", HttpMethod.POST, new HttpEntity<>(depReq, headers), DepartmentResponse.class);

        assertEquals(201, depRes.getStatusCode().value());
        assertNotNull(depRes.getBody());
        Long deptId = depRes.getBody().getId();

        // 2. Create employee
        CreateEmployeeRequest empReq = new CreateEmployeeRequest();
        empReq.setFirstName("John");
        empReq.setLastName("Doe");
        empReq.setEmail("john.doe@test.com");
        empReq.setDepartmentId(deptId);

        ResponseEntity<EmployeeResponse> empRes = restTemplate.exchange(
                "/api/employees", HttpMethod.POST, new HttpEntity<>(empReq, headers), EmployeeResponse.class);

        assertEquals(201, empRes.getStatusCode().value());
        assertNotNull(empRes.getBody());
        Long empId = empRes.getBody().getId();
        assertNotNull(empId);

        // 3. Get employee
        ResponseEntity<EmployeeResponse> getRes = restTemplate.exchange(
                "/api/employees/" + empId, HttpMethod.GET, new HttpEntity<>(headers), EmployeeResponse.class);
        assertEquals(200, getRes.getStatusCode().value());
        assertNotNull(getRes.getBody());
        assertEquals("John", getRes.getBody().getFirstName());

        // 4. Update employee
        UpdateEmployeeRequest updateReq = new UpdateEmployeeRequest();
        updateReq.setFirstName("Jane");
        ResponseEntity<EmployeeResponse> updateRes = restTemplate.exchange(
                "/api/employees/" + empId, HttpMethod.PUT, new HttpEntity<>(updateReq, headers), EmployeeResponse.class);
        assertEquals(200, updateRes.getStatusCode().value());
        assertNotNull(updateRes.getBody());
        assertEquals("Jane", updateRes.getBody().getFirstName());

        // 5. Search employees
        ResponseEntity<String> searchRes = restTemplate.exchange(
                "/api/employees/search?query=Jane", HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(200, searchRes.getStatusCode().value());

        // 6. Get employees by department
        ResponseEntity<String> byDeptRes = restTemplate.exchange(
                "/api/employees/department/" + deptId, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        assertEquals(200, byDeptRes.getStatusCode().value());
    }
}
