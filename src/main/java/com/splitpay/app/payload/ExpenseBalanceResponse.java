package com.splitpay.app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseBalanceResponse {
    private Long userId;
    private String userName;
    private List<TransactionDetail> transactions;
}

