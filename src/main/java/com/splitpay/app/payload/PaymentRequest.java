package com.splitpay.app.payload;

import lombok.Data;

@Data
public class PaymentRequest {
    Long groupId;
    Long payeeId;
    Double amount;
    String paymentChannelType;
}
