package com.pia.ticketmanagement.exception;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ApiErrorResponse {
    private int status;
    private String message;
    private LocalDateTime timestamp;
}