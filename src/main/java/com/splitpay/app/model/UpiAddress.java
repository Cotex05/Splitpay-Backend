package com.splitpay.app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "upi_addresses", uniqueConstraints = {@UniqueConstraint(columnNames = "upi_address")})
public class UpiAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long upiAddressId;

    @Column(name = "upi_address", nullable = false)
    private String upiAddress;

    @ManyToOne
    @JoinColumn(name = "payment_channel_id")
    private PaymentChannel paymentChannel;

    private LocalDateTime createdAt;

}
