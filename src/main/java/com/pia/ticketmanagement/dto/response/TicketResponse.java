package com.pia.ticketmanagement.dto.response;

import com.pia.ticketmanagement.model.TicketPriority;
import com.pia.ticketmanagement.model.TicketStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TicketResponse {

    private Long id;
    private String ticketNumber;

    private Long customerId;
    private String customerName;
    private String customerPhone;

    private String category;
    private String subCategory;

    private String issueProvince;
    private String issueDistrict;

    private TicketStatus status;
    private TicketPriority priority;

    private String description;
    private String resolutionNote;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;

    private Long assignedEmployeeId;
    private String assignedEmployeeName;
}