package com.pia.ticketmanagement.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private LocalDateTime createdAt;
}
