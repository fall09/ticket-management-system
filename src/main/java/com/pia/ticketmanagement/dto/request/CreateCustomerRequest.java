package com.pia.ticketmanagement.dto.request;

import com.pia.ticketmanagement.model.CustomerStatus;
import lombok.Data;

@Data

public class CreateCustomerRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Long provinceId;
    private Long districtId;
    private CustomerStatus status;
}