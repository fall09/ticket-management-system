package com.pia.ticketmanagement.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CustomerStatus newStatus;

    @Enumerated(EnumType.STRING)
    private InactiveReason inactiveReason;

    @Enumerated(EnumType.STRING)
    private SuspendedReason suspendedReason;

    @Column(length = 500)
    private String note;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}