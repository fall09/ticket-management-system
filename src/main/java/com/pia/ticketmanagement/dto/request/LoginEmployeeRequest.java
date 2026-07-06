package com.pia.ticketmanagement.dto.request;

import lombok.Data;

@Data
public class LoginEmployeeRequest {

    private String email;
    private String password;
}