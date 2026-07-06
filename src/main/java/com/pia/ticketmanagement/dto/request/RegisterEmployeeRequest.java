package com.pia.ticketmanagement.dto.request;

import lombok.Data;

@Data
public class RegisterEmployeeRequest {

    private String firstName;
    private String lastName;
    private String email;
    private String password;
}