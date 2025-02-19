package com.splitpay.app.payload;

import lombok.Data;

import java.time.LocalDate;

@Data
public class WeeklyExpenseStatsResponse {
    private String weekday;
    private LocalDate date;
    private Double spentAmount;

}
