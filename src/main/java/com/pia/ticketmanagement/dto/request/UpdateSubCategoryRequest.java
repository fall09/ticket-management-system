package com.pia.ticketmanagement.dto.request;

import com.pia.ticketmanagement.model.TicketPriority;
import lombok.Data;

@Data
public class UpdateSubCategoryRequest {

    private String name;

    private boolean locationRequired;

    private TicketPriority defaultPriority;
}