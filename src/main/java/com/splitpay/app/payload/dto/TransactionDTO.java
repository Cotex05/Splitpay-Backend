package com.splitpay.app.payload.dto;

import com.splitpay.app.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {
    private Long transactionId;
    private UserDTO payer;
    private UserDTO payee;
    private Double amount;

}
