package com.ets.dto.response;

import com.ets.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripPassengerResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String pickupStopName;
    private String dropoffStopName;
    private BookingStatus bookingStatus;
    private LocalDateTime bookedAt;
}
