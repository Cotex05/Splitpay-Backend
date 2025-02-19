package com.splitpay.app.payload.dto;

import com.splitpay.app.model.Expense;
import com.splitpay.app.model.User;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseShareDTO {
    private Long expenseShareId;

    private UserDTO user;

    private double amount;

}
