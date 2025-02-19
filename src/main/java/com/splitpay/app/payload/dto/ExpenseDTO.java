package com.splitpay.app.payload.dto;

import com.splitpay.app.model.ExpenseShare;
import com.splitpay.app.model.Group;
import com.splitpay.app.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseDTO {

    private Long expenseId;

    private GroupDTO group;

    private double amount;

    private String description;

    private String category;

    private UserDTO paidBy;

    private LocalDateTime createdAt;

    private List<ExpenseShareDTO> shares;
}
