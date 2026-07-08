package com.pia.ticketmanagement.dto.response;

import com.pia.ticketmanagement.model.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketStatusHistoryResponse {

    private Long id;
    private TicketStatus oldStatus;
    private TicketStatus newStatus;
    private String note;
    private LocalDateTime changedAt;
}