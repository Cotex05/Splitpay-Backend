package com.splitpay.app.service;

import com.splitpay.app.exception.APIException;
import com.splitpay.app.exception.ResourceNotFoundException;
import com.splitpay.app.model.Group;
import com.splitpay.app.model.GroupMember;
import com.splitpay.app.model.User;
import com.splitpay.app.payload.dto.GroupDTO;
import com.splitpay.app.payload.dto.UserDTO;
import com.splitpay.app.repository.GroupMemberRepository;
import com.splitpay.app.repository.GroupRepository;
import com.splitpay.app.repository.UserRepository;
import com.splitpay.app.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GroupMemberServiceImpl implements GroupMemberService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthUtil authUtil;

    private final ModelMapper modelMapper = new ModelMapper();


    @Transactional
    public void addOneGroupMember(Long groupId, String username) {
        // Validation new user is going to be added only be the group creator
        User currUser =  authUtil.loggedInUser();
        // find out group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "groupId", groupId));

        if(!Objects.equals(currUser.getUserId(), group.getCreatedBy().getUserId())) {
            throw new APIException("User not allowed to add group member");
        }
            // find out User
            User user = userRepository.findByUserName(username)
                    .orElseThrow(() -> new ResourceNotFoundException("username", "username", username));
        // check if user is already in the group
        List<User> groupMembers = groupMemberRepository.findByGroupId(groupId)
                .stream().map(GroupMember::getUser).toList();

        if(groupMembers.contains(user)) {
            throw new APIException(username + " is already in group " + groupId);
        }

        GroupMember groupMember = new GroupMember();
        groupMember.setUser(user);
        groupMember.setGroup(group);
        groupMember.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(groupMember);
    }

    @Transactional
    @Override
    public void removeGroupMember(Long groupId, String username) {
        User currUser =  authUtil.loggedInUser();
        // find out User
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("username", "username", username));
        // find out group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "groupId", groupId));


        if(!Objects.equals(currUser.getUserId(), group.getCreatedBy().getUserId())) {
            throw new APIException("User not allowed to remove group member");
        }

        // check if user exists in the group
        List<User> groupMembers = groupMemberRepository.findByGroupId(groupId)
                .stream().map(GroupMember::getUser).toList();

        if(!groupMembers.contains(user)) {
            throw new APIException(username + " is not present in group " + groupId);
        }

        groupMemberRepository.deleteGroupMemberByGroupIdAndUserId(group.getGroupId(), user.getUserId());
    }

    @Override
    public List<UserDTO> getGroupMembers(Long groupId) {
        if(!groupRepository.existsById(groupId)) {
            throw new ResourceNotFoundException("Group", "groupId", groupId);
        }
        List<UserDTO> groupMembers = groupMemberRepository.findByGroupId(groupId)
                .stream()
                .map((group) -> modelMapper.map(group.getUser(), UserDTO.class)) // Extract the User entity from GroupMember
                .toList();
        return groupMembers;
    }

    @Override
    @Transactional
    public void addMultipleGroupMembers(Long groupId, List<String> usernames) {
        for(String username : usernames) {
            addOneGroupMember(groupId, username);
        }
    }

    @Override
    public void addGroupMemberFromGroupCode(String groupCode) {
        Group group = groupRepository.findByGroupCode(groupCode)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "groupCode", groupCode));

        // current logged in user will join using this invitation of groupcode
        User currUser =  authUtil.loggedInUser();

        // check if user is already in the group
        List<User> groupMembers = groupMemberRepository.findByGroupId(group.getGroupId())
                .stream().map(GroupMember::getUser).toList();

        if(groupMembers.contains(currUser)) {
            throw new APIException(currUser.getUserName() + " is already in group " + group.getGroupName());
        }

        GroupMember groupMember = new GroupMember();
        groupMember.setUser(currUser);
        groupMember.setGroup(group);
        groupMember.setJoinedAt(LocalDateTime.now());

        groupMemberRepository.save(groupMember);

    }
}
