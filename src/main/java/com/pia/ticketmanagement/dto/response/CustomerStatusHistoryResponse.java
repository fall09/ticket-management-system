package com.pia.ticketmanagement.dto.response;

import com.pia.ticketmanagement.model.CustomerStatus;
import com.pia.ticketmanagement.model.InactiveReason;
import com.pia.ticketmanagement.model.SuspendedReason;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerStatusHistoryResponse {

    private Long id;
    private CustomerStatus oldStatus;
    private CustomerStatus newStatus;
    private InactiveReason inactiveReason;
    private SuspendedReason suspendedReason;
    private String note;
    private LocalDateTime changedAt;
}