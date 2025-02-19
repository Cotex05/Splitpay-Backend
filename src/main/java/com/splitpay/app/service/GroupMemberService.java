package com.splitpay.app.service;

import com.splitpay.app.payload.dto.UserDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GroupMemberService {

    @Transactional
    void addOneGroupMember(Long groupId, String username);

    @Transactional
    void removeGroupMember(Long groupId, String username);

    List<UserDTO> getGroupMembers(Long groupId);

    void addMultipleGroupMembers(Long groupId, List<String> usernames);

    void addGroupMemberFromGroupCode(String groupCode);
}
