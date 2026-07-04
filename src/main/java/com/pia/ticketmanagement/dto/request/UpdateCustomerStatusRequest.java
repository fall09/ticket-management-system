package com.pia.ticketmanagement.dto.request;

import com.pia.ticketmanagement.model.CustomerStatus;
import com.pia.ticketmanagement.model.InactiveReason;
import com.pia.ticketmanagement.model.SuspendedReason;
import lombok.Data;

@Data
public class UpdateCustomerStatusRequest {

    private CustomerStatus status;

    private InactiveReason inactiveReason;

    private SuspendedReason suspendedReason;

    private String note;
}