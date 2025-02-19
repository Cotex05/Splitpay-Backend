package com.splitpay.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private String referenceId;

    @ManyToOne
    @JoinColumn(name="payment_channel_id", nullable=false)
    private PaymentChannel paymentChannel;

    @ManyToOne
    @JoinColumn(name = "payee_id", nullable = false)
    private User payee;

    @ManyToOne
    @JoinColumn(name = "payer_id", nullable = false)
    private User payer;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    private Double amount;

    private LocalDateTime paymentDate;
}
