package com.pia.ticketmanagement.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ticket_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    private TicketStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus newStatus;

    @Column(length = 1000)
    private String note;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}