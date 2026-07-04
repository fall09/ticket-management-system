package com.pia.ticketmanagement.dto.request;

import lombok.Data;

@Data
public class UpdateCustomerRequest {

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String email;
    private Long provinceId;
    private Long districtId;
}