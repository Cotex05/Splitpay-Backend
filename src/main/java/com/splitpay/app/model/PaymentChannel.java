package com.splitpay.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payment_channels")
public class PaymentChannel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentChannelId;

    private String paymentChannelType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

}
