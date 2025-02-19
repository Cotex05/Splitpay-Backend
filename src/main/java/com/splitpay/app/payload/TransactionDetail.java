package com.splitpay.app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDetail {
    private String fromUserName; // Debtor
    private String toUserName;   // Creditor
    private double amount;       // Amount transferred
}
