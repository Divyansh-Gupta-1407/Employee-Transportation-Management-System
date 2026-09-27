package com.ets.controller;

import com.ets.BaseIntegrationTest;
import com.ets.dto.request.LoginRequest;
import com.ets.dto.request.RegisterRequest;
import com.ets.dto.response.AuthResponse;
import com.ets.entity.Role;
import com.ets.repository.RoleRepository;
import com.ets.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class AuthControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        Role role = new Role();
        role.setName("ROLE_EMPLOYEE");
        roleRepository.save(role);
    }

    @Test
    void fullRegisterAndLoginFlow() {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setPhone("1234567890");

        ResponseEntity<String> registerResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, String.class);
        assertEquals(HttpStatus.CREATED, registerResponse.getStatusCode());
        assertEquals("User registered successfully", registerResponse.getBody());

        ResponseEntity<String> duplicateRegisterResponse = restTemplate.postForEntity("/api/auth/register", registerRequest, String.class);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, duplicateRegisterResponse.getStatusCode()); // Basic error handling mappings might map runtime exception to 500 without global handler.

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("testuser");
        loginRequest.setPassword("password123");

        ResponseEntity<AuthResponse> loginResponse = restTemplate.postForEntity("/api/auth/login", loginRequest, AuthResponse.class);
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        assertNotNull(loginResponse.getBody());
        assertNotNull(loginResponse.getBody().getAccessToken());

        LoginRequest invalidLoginRequest = new LoginRequest();
        invalidLoginRequest.setUsernameOrEmail("testuser");
        invalidLoginRequest.setPassword("wrongpassword");

        ResponseEntity<String> invalidLoginResponse = restTemplate.postForEntity("/api/auth/login", invalidLoginRequest, String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, invalidLoginResponse.getStatusCode());
    }

    @Test
    void registerWithInvalidData_ShouldReturn400() {
        RegisterRequest registerRequest = new RegisterRequest();
        // empty fields
        ResponseEntity<String> response = restTemplate.postForEntity("/api/auth/register", registerRequest, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
