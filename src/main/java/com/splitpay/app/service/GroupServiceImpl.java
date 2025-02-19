package com.splitpay.app.service;

import com.splitpay.app.exception.ResourceNotFoundException;
import com.splitpay.app.model.Group;
import com.splitpay.app.model.GroupMember;
import com.splitpay.app.model.User;
import com.splitpay.app.payload.dto.GroupDTO;
import com.splitpay.app.payload.dto.UserDTO;
import com.splitpay.app.repository.GroupMemberRepository;
import com.splitpay.app.repository.GroupRepository;

import com.splitpay.app.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberService groupMemberService;

    @Autowired
    private AuthUtil authUtil;

    private final ModelMapper modelMapper = new ModelMapper();
    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Override
    public GroupDTO createGroup(String groupName) {
        // Create a new group
        Group group = new Group();
        group.setGroupName(groupName);
        User user = authUtil.loggedInUser();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        String groupCode = UUID.randomUUID().toString().substring(0, 6);
        group.setGroupCode(groupCode);
        group.setCreatedBy(user);
        group.setCreatedAt(LocalDateTime.now());

        // Save the group to the database
        Group savedGroup = groupRepository.save(group);

        groupMemberService.addOneGroupMember(savedGroup.getGroupId(), user.getUserName());

        GroupDTO groupDTO = modelMapper.map(savedGroup, GroupDTO.class);
        groupDTO.setCreatedBy(userDTO);
        return groupDTO;
    }

    @Override
    public GroupDTO getGroupDetails(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));
        return modelMapper.map(group, GroupDTO.class);

    }

    @Override
    public List<GroupDTO> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        List<GroupDTO> groupDTOList = groups.stream()
                .map((group) -> modelMapper.map(group, GroupDTO.class))
                .toList();
        return groupDTOList;
    }

    @Override
    public List<GroupDTO> getUsersAllGroups() {
        User currUser = authUtil.loggedInUser();
        List<GroupMember> groupMemberList = groupMemberRepository.findAllByUserId(currUser.getUserId());
        List<Long> groupIds = groupMemberList.stream()
                .map((groupMember -> groupMember.getGroup().getGroupId()))
                .toList();
        List<Group> groups = groupRepository.findAllByGroupId(groupIds);
        List<GroupDTO> groupDTOList = groups.stream()
                .map((group) -> modelMapper.map(group, GroupDTO.class))
                .toList();
        return groupDTOList;
    }


}
