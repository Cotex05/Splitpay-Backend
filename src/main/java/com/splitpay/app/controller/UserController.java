package com.splitpay.app.controller;

import com.splitpay.app.exception.APIException;
import com.splitpay.app.exception.ResourceNotFoundException;
import com.splitpay.app.model.User;
import com.splitpay.app.payload.UserDetailResponse;
import com.splitpay.app.payload.dto.UserDTO;
import com.splitpay.app.repository.UserRepository;
import com.splitpay.app.service.UserService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserDetails(@PathVariable String username) {
        UserDTO userDTO = userService.getUserByUsername(username);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUser(@RequestParam String query) {
        // ✅ Validate input: Require at least 3 characters
        if (query.trim().length() < 3) {
            throw new APIException("Username must be at least 3 characters");
        }

        List<UserDTO> userDTOList = userService.findUserByQuery(query);
        if (userDTOList.isEmpty()) {
            throw new ResourceNotFoundException("Users", "username", query);
        }

        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

}
