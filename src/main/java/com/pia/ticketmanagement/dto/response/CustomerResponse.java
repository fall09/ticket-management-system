package com.pia.ticketmanagement.dto.response;

import com.pia.ticketmanagement.model.CustomerStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerResponse {

    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private String province;
    private String district;

    private CustomerStatus status;
}