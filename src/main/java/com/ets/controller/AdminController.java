package com.ets.controller;

import com.ets.dto.response.DashboardResponse;
import com.ets.dto.response.EmployeeResponse;
import com.ets.dto.response.TripReportResponse;
import com.ets.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping("/reports/trips")
    public ResponseEntity<TripReportResponse> getTripReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(adminService.getTripReport(from, to));
    }

    @PutMapping("/employees/{id}/status")
    public ResponseEntity<EmployeeResponse> toggleEmployeeStatus(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.toggleEmployeeStatus(id));
    }
}
