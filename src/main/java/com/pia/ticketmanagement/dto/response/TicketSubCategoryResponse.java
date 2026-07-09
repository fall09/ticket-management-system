package com.pia.ticketmanagement.dto.response;

import com.pia.ticketmanagement.model.TicketPriority;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TicketSubCategoryResponse {

    private Long id;

    private String name;

    private Long categoryId;

    private String categoryName;

    private boolean locationRequired;

    private TicketPriority defaultPriority;

    private boolean availableForNewCustomer;
}