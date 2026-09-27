package com.ets.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterDriverRequest {
    @NotNull
    private Long employeeId;

    @NotBlank
    @Size(max = 50)
    private String licenseNumber;

    @NotNull
    @Future
    private LocalDate licenseExpiry;
}
