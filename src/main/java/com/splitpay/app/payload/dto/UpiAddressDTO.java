package com.splitpay.app.payload.dto;

import com.splitpay.app.model.PaymentChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpiAddressDTO {
    private Long upiAddressId;
    private String upiAddress;
    private PaymentChannelDTO paymentChannel;
    private LocalDateTime createdAt;
}
