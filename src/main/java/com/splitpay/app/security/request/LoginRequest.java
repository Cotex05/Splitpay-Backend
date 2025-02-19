package com.splitpay.app.security.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    private String profileId;

    private String email;

    private String username;
    private String password;

}