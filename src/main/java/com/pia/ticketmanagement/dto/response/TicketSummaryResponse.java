package com.pia.ticketmanagement.dto.response;

import lombok.AllArgsConstructor;

import lombok.Builder;

import lombok.Data;

import lombok.NoArgsConstructor;

@Data

@Builder

@NoArgsConstructor

@AllArgsConstructor

public class TicketSummaryResponse {

    private Long totalTickets;

    private Long openTickets;

    private Long inProgressTickets;

    private Long onHoldTickets;

    private Long resolvedTickets;

    private Long closedTickets;

    private Long criticalTickets;

    private Integer resolutionRate;

}