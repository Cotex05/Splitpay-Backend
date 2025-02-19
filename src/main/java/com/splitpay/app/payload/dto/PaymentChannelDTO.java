package com.splitpay.app.payload.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentChannelDTO {
    private Long paymentChannelId;

    private String paymentChannelType;

    private UserDTO user;
}
