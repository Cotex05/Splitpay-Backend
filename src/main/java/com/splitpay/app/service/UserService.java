package com.splitpay.app.service;

import com.splitpay.app.payload.ProfileDataRequest;
import com.splitpay.app.payload.dto.UserDTO;

import java.util.List;

public interface UserService {
    UserDTO getUserByUsername(String username);

    void checkUserExistsInGroup(Long groupId);

    List<UserDTO> findUserByQuery(String query);

    void updateUserProfile(ProfileDataRequest profileDataRequest);
}
