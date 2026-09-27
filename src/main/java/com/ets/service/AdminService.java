package com.ets.service;

import com.ets.dto.response.DashboardResponse;
import com.ets.dto.response.EmployeeResponse;
import com.ets.dto.response.TripReportResponse;

import java.time.LocalDateTime;

public interface AdminService {
    DashboardResponse getDashboard();
    TripReportResponse getTripReport(LocalDateTime from, LocalDateTime to);
    EmployeeResponse toggleEmployeeStatus(Long employeeId);
}
