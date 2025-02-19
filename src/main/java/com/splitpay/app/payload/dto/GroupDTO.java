package com.splitpay.app.payload.dto;

import com.splitpay.app.model.Expense;
import com.splitpay.app.model.GroupMember;
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
public class GroupDTO {
    private Long groupId;

    private String groupName;

    private String groupCode;

    private UserDTO createdBy;

    private LocalDateTime createdAt;

//    private List<GroupMemberDTO> members;
//
//    private List<ExpenseDTO> expenses;
}
