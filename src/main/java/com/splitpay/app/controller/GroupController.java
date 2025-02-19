package com.splitpay.app.controller;

import com.splitpay.app.exception.APIException;
import com.splitpay.app.payload.dto.GroupDTO;
import com.splitpay.app.payload.dto.UserDTO;
import com.splitpay.app.security.response.MessageResponse;
import com.splitpay.app.service.GroupMemberService;
import com.splitpay.app.service.GroupService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupMemberService groupMemberService;

    @PostMapping("/operations/create")
    private ResponseEntity<GroupDTO> createGroup(@RequestParam String groupName) {
        if (groupName.isEmpty()) {
            throw new APIException("Group name cannot be empty!");
        }
        GroupDTO groupDTO = groupService.createGroup(groupName);
        return new ResponseEntity<>(groupDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{groupId}")
    private ResponseEntity<GroupDTO> getGroup(@PathVariable Long groupId) {
        GroupDTO groupDTO = groupService.getGroupDetails(groupId);
        return new ResponseEntity<>(groupDTO, HttpStatus.OK);
    }

    @GetMapping("/all")
    private ResponseEntity<List<GroupDTO>> getAllGroups() {
        List<GroupDTO> groupDTOs = groupService.getAllGroups();
        return new ResponseEntity<>(groupDTOs, HttpStatus.OK);
    }


    @PostMapping("/{groupId}/member/add")
    public ResponseEntity<?> addUserToGroup(@PathVariable Long groupId, @RequestParam String username){
        groupMemberService.addOneGroupMember(groupId, username);
        return ResponseEntity.ok().body(new MessageResponse(String.format(username + " added to the group " + groupId)));
    }

    @PostMapping("/member/invite/add")
    public ResponseEntity<?> addUserToGroupFromInvitation(@RequestParam String groupCode){
        groupMemberService.addGroupMemberFromGroupCode(groupCode);
        return ResponseEntity.ok().body(new MessageResponse("You have been added to the group!"));
    }


    @PostMapping("/{groupId}/members/add")
    public ResponseEntity<?> addMultipleUsersToGroup(@PathVariable Long groupId, @RequestBody List<String> usernames){
        groupMemberService.addMultipleGroupMembers(groupId, usernames);
        return ResponseEntity.ok().body(new MessageResponse(String.format(usernames + " added to the group " + groupId)));
    }

    @DeleteMapping("/{groupId}/members/remove")
    public ResponseEntity<?> removeUserFromGroup(@PathVariable Long groupId, @RequestParam String username){
        groupMemberService.removeGroupMember(groupId, username);
        return ResponseEntity.ok().body(new MessageResponse(String.format(username + " removed from the group " + groupId)));
    }

    @GetMapping("/{groupId}/members/all")
    private ResponseEntity<List<UserDTO>> getAllGroups(@PathVariable Long groupId) {
        List<UserDTO> groupMembers = groupMemberService.getGroupMembers(groupId);
        return new ResponseEntity<>(groupMembers, HttpStatus.OK);
    }

    @GetMapping("/user")
    private ResponseEntity<List<GroupDTO>> getUsersAllGroups() {
        List<GroupDTO> groupDTOs = groupService.getUsersAllGroups();
        return new ResponseEntity<>(groupDTOs, HttpStatus.OK);
    }

}
