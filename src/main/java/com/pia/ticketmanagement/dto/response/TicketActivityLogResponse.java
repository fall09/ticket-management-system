
package com.pia.ticketmanagement.dto.response;

import com.pia.ticketmanagement.model.TicketActivityType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TicketActivityLogResponse {

    private Long id;
    private TicketActivityType type;
    private String oldValue;
    private String newValue;
    private String note;
    private Long employeeId;
    private String employeeName;
    private LocalDateTime createdAt;
}
