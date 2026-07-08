package com.pia.ticketmanagement.dto.request;

import com.pia.ticketmanagement.model.TicketPriority;
import com.pia.ticketmanagement.model.TicketStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTicketRequest {
    private TicketStatus status;
    private TicketPriority priority;
    private String description;
    private String note;
}