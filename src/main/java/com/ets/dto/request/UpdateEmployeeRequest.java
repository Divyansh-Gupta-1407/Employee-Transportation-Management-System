package com.ets.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateEmployeeRequest {

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @Email
    private String email;

    @Size(max = 20)
    private String phone;

    private Long departmentId;

    @Size(max = 100)
    private String designation;

    private String address;

    private Boolean active;
}
