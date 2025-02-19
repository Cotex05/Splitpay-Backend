package com.splitpay.app.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long paymentId;
    private String referenceId;
    private Double amount;
    private UserDTO payer;
    private UserDTO payee;
    private GroupDTO group;
    private PaymentChannelDTO paymentChannel;
    private LocalDateTime paymentDate;
}
