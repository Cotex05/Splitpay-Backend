package com.splitpay.app.controller;

import com.splitpay.app.config.AppConstants;
import com.splitpay.app.payload.*;
import com.splitpay.app.payload.dto.ExpenseDTO;
import com.splitpay.app.security.response.MessageResponse;
import com.splitpay.app.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/groups/{groupId}/add")
    public ResponseEntity<ExpenseDTO> createNewExpense(@PathVariable Long groupId, @RequestBody @Valid ExpenseRequest expenseRequest) {
        ExpenseDTO expenseDTO = expenseService.addExpense(groupId, expenseRequest);
        return new ResponseEntity<>(expenseDTO, HttpStatus.CREATED);
    }

    @DeleteMapping("/{expenseId}/groups/{groupId}/remove")
    public ResponseEntity<?> removeExpense(@PathVariable Long groupId, @PathVariable Long expenseId) {
        expenseService.removeExpense(expenseId, groupId);
        return ResponseEntity.ok().body(new MessageResponse("Expense removed successfully"));
    }

    @GetMapping("/groups/{groupId}")
    public ResponseEntity<ExpenseResponse> getAllExpensesOfGroup(@PathVariable Long groupId,
                                                                  @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                  @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                  @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_EXPENSE_BY, required = false) String sortBy,
                                                                  @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
                                                                  Sort sort) {
        ExpenseResponse expenses = expenseService.getAllExpensesFromGroup(groupId, pageNumber, pageSize, sortBy, sortOrder);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @GetMapping("/{expenseId}/groups/{groupId}")
    public ResponseEntity<ExpenseDTO> getExpense(@PathVariable Long groupId, @PathVariable Long expenseId) {
        ExpenseDTO expense = expenseService.getExpense(expenseId, groupId);
        return new ResponseEntity<>(expense, HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/user")
    public ResponseEntity<List<ExpenseDTO>> getExpensesOfUser(@PathVariable Long groupId) {
        List<ExpenseDTO> expenses = expenseService.getUsersExpensesFromGroup(groupId);
        return new ResponseEntity<>(expenses, HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/balance-transactions")
    public ResponseEntity<List<ExpenseBalanceResponse>> getExpenseBalanceOfGroup(@PathVariable Long groupId) {
        List<ExpenseBalanceResponse> expenseBalanceResponse = expenseService.getExpenseBalanceOfGroup(groupId);
        return new ResponseEntity<>(expenseBalanceResponse, HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/balance-graph")
    public ResponseEntity<List<BalanceGraphResponse>> getExpensesOfGroup(@PathVariable Long groupId) {
        List<BalanceGraphResponse> balanceGraphResponseList = expenseService.getBalanceGraphOfGroup(groupId);
        return new ResponseEntity<>(balanceGraphResponseList, HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/user/balance-graph")
    public ResponseEntity<UserBalanceGraphResponse> getUserExpensesOfGroup(@PathVariable Long groupId) {
        UserBalanceGraphResponse userBalanceGraphResponse = expenseService.getUserBalanceGraphOfGroup(groupId);
        return new ResponseEntity<>(userBalanceGraphResponse, HttpStatus.OK);
    }

    @GetMapping("/groups/{groupId}/user/cashflow")
    public ResponseEntity<BalanceResponse> getUserBalanceOfGroup(@PathVariable Long groupId) {
        BalanceResponse balanceResponse = expenseService.getUserBalanceInGroup(groupId);
        return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
    }

    @GetMapping("/user/stats")
    public ResponseEntity<ExpenseStatsResponse> getExpenseStatsOfUser() {
        ExpenseStatsResponse expenseStatsResponse = expenseService.getUserExpenseStats();
        return new ResponseEntity<>(expenseStatsResponse, HttpStatus.OK);
    }

    @GetMapping("/user/stats/lastWeek")
    public ResponseEntity<List<WeeklyExpenseStatsResponse>> getLastWeekExpenseStats() {
        List<WeeklyExpenseStatsResponse> response = expenseService.getWeeklyUserExpenseStats();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
