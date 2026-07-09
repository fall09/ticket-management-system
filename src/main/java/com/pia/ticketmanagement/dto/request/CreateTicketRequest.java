package com.pia.ticketmanagement.dto.request;


import com.pia.ticketmanagement.model.TicketPriority;
import lombok.Data;

@Data
public class CreateTicketRequest {

    private Long customerId;
    private Long categoryId;
    private Long subCategoryId;
    private Long provinceId;
    private Long districtId;
    private String description;

    private TicketPriority priority;
    private Boolean assignToMe;
}