package com.pia.ticketmanagement.dto.response;

import com.pia.ticketmanagement.model.TicketPriority;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubCategoryResponse {

    private Long id;
    private String name;
    private Boolean locationRequired;
    private TicketPriority defaultPriority;
}