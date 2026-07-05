package com.pia.ticketmanagement.dto.request;


import lombok.Data;

@Data
public class CreateTicketRequest {

    private Long customerId;
    private Long categoryId;
    private Long subCategoryId;
    private Long provinceId;
    private Long districtId;
    private String description;
}