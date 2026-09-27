package com.ets.controller;

import com.ets.BaseIntegrationTest;
import com.ets.dto.request.*;
import com.ets.dto.response.*;
import com.ets.entity.*;
import com.ets.enums.*;
import com.ets.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TripControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;
    private Long routeId;
    private Long vehicleId;
    private Long driverId;
    private Long employeeId;

    @BeforeEach
    void setUp() {
        driverRepository.deleteAll();
        employeeRepository.deleteAll();
        vehicleRepository.deleteAll();
        routeRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        departmentRepository.deleteAll();

        Role adminRole = Role.builder().name("ROLE_ADMIN").build();
        adminRole = roleRepository.save(adminRole);
        Role employeeRole = Role.builder().name("ROLE_EMPLOYEE").build();
        roleRepository.save(employeeRole);

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

        Department dept = Department.builder().name("IT").code("IT").build();
        dept = departmentRepository.save(dept);

        Employee emp = Employee.builder()
                .employeeCode("EMP001")
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .department(dept)
                .active(true)
                .build();
        emp = employeeRepository.save(emp);
        employeeId = emp.getId();

        Vehicle vehicle = Vehicle.builder()
                .registrationNumber("AB-1234")
                .model("Bus A")
                .capacity(30)
                .type(VehicleType.BUS)
                .active(true)
                .build();
        vehicle = vehicleRepository.save(vehicle);
        vehicleId = vehicle.getId();

        Route route = Route.builder()
                .name("Route 1")
                .source("Office")
                .destination("Suburb")
                .active(true)
                .build();
        route = routeRepository.save(route);
        routeId = route.getId();

        Driver driver = Driver.builder()
                .employee(emp)
                .licenseNumber("LIC123")
                .licenseExpiry(LocalDate.now().plusYears(1))
                .available(true)
                .build();
        driver = driverRepository.save(driver);
        driverId = driver.getId();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.set("Content-Type", "application/json");
        return headers;
    }

    @Test
    void testTripLifecycle() {
        CreateTripRequest createReq = new CreateTripRequest();
        createReq.setRouteId(routeId);
        createReq.setVehicleId(vehicleId);
        createReq.setDriverId(driverId);
        createReq.setScheduledDeparture(LocalDateTime.now().plusDays(1));
        createReq.setScheduledArrival(LocalDateTime.now().plusDays(1).plusHours(1));

        ResponseEntity<TripResponse> createRes = restTemplate.exchange(
                "/api/trips",
                HttpMethod.POST,
                new HttpEntity<>(createReq, getHeaders()),
                TripResponse.class
        );
        assertEquals(201, createRes.getStatusCode().value());
        assertNotNull(createRes.getBody());
        Long tripId = createRes.getBody().getId();

        AddPassengerRequest addReq = new AddPassengerRequest();
        addReq.setEmployeeId(employeeId);
        ResponseEntity<Void> addRes = restTemplate.exchange(
                "/api/trips/" + tripId + "/passengers",
                HttpMethod.POST,
                new HttpEntity<>(addReq, getHeaders()),
                Void.class
        );
        assertEquals(201, addRes.getStatusCode().value());

        UpdateTripStatusRequest updateReq = new UpdateTripStatusRequest();
        updateReq.setStatus(TripStatus.IN_PROGRESS);
        ResponseEntity<TripResponse> updateRes1 = restTemplate.exchange(
                "/api/trips/" + tripId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(updateReq, getHeaders()),
                TripResponse.class
        );
        assertEquals(200, updateRes1.getStatusCode().value());
        assertNotNull(updateRes1.getBody());
        assertEquals(TripStatus.IN_PROGRESS, updateRes1.getBody().getStatus());

        updateReq.setStatus(TripStatus.COMPLETED);
        ResponseEntity<TripResponse> updateRes2 = restTemplate.exchange(
                "/api/trips/" + tripId + "/status",
                HttpMethod.PATCH,
                new HttpEntity<>(updateReq, getHeaders()),
                TripResponse.class
        );
        assertEquals(200, updateRes2.getStatusCode().value());
        assertNotNull(updateRes2.getBody());
        assertEquals(TripStatus.COMPLETED, updateRes2.getBody().getStatus());
    }
}
