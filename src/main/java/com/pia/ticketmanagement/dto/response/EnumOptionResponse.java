package com.pia.ticketmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnumOptionResponse {
    private String value;
    private String displayName;
}