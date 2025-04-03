package com.splitpay.app.service;

import com.splitpay.app.exception.APIException;
import com.splitpay.app.exception.ResourceNotFoundException;
import com.splitpay.app.model.Group;
import com.splitpay.app.model.GroupMember;
import com.splitpay.app.model.User;
import com.splitpay.app.payload.ProfileDataRequest;
import com.splitpay.app.payload.dto.UserDTO;
import com.splitpay.app.repository.GroupMemberRepository;
import com.splitpay.app.repository.GroupRepository;
import com.splitpay.app.repository.UserRepository;
import com.splitpay.app.util.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UserRepository userRepository;

    private ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private AuthUtil authUtil;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMemberRepository groupMemberRepository;

    @Override
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return userDTO;
    }

    @Override
    public void checkUserExistsInGroup(Long groupId) {
        User currUser = authUtil.loggedInUser();
        // Validate group
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Group", "id", groupId));

        // check whether user exists in group
        GroupMember groupMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currUser.getUserId());

        if (groupMember == null) {
            throw new APIException("User doesn't exists in the group: " + groupId);
        }

    }

    @Override
    public List<UserDTO> findUserByQuery(String query) {
        User currUser = authUtil.loggedInUser();
        List<User> foundUsers = userRepository.findAllByUserNameContainingIgnoreCase(query);
        foundUsers.remove(currUser); // exclude current user from search
        return foundUsers.stream().map(user -> modelMapper.map(user, UserDTO.class)).toList();
    }

    @Override
    public void updateUserProfile(ProfileDataRequest profileDataRequest) {
        User currUser = authUtil.loggedInUser();
        if(!currUser.getEmail().equals(profileDataRequest.getEmail())) {
            throw new APIException("User doesn't exists in the profile");
        }
        User user = userRepository.findByEmail(profileDataRequest.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User", "email", profileDataRequest.getEmail()));
        // only fullname updated
        user.setFullName(profileDataRequest.getFullName());
        userRepository.save(user);
    }


}
