package com.splitpay.app.service;

import com.splitpay.app.payload.dto.GroupDTO;

import java.util.List;

public interface GroupService {

    GroupDTO createGroup(String groupName);

    GroupDTO getGroupDetails(Long groupId);

    List<GroupDTO> getAllGroups();

    List<GroupDTO> getUsersAllGroups();
}
