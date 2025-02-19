package com.splitpay.app.payload;

import lombok.Data;

@Data
public class ExpenseStatsResponse {
    private Double totalAmountSpent;
    private Double totalAmountIn;
    private Double totalAmountOut;
}
