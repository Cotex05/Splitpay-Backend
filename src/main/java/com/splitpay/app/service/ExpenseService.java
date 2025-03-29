package com.splitpay.app.service;

import com.splitpay.app.payload.*;
import com.splitpay.app.payload.dto.ExpenseDTO;

import java.util.List;

public interface ExpenseService {
    ExpenseDTO addExpense(Long groupId, ExpenseRequest expenseRequest);

    void removeExpense(Long expenseId, Long groupId);

    ExpenseResponse getAllExpensesFromGroup(Long groupId, Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);

    List<ExpenseDTO> getUsersExpensesFromGroup(Long groupId);

    ExpenseDTO getExpense(Long expenseId, Long groupId);

    List<ExpenseBalanceResponse> getExpenseBalanceOfGroup(Long groupId);

    List<BalanceGraphResponse> getBalanceGraphOfGroup(Long groupId);

    UserBalanceGraphResponse getUserBalanceGraphOfGroup(Long groupId);

    BalanceResponse getUserBalanceInGroup(Long groupId);

    ExpenseStatsResponse getUserExpenseStats();

    List<WeeklyExpenseStatsResponse> getWeeklyUserExpenseStats();
}
