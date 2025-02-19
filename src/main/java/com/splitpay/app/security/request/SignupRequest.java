package com.splitpay.app.security.request;
import java.util.Set;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SignupRequest {

    @NotBlank
    private String fullName;

    private String profileId;

    private String photoUrl;

    @NotBlank
    @Size(min = 3, max = 20)
    private String username;
    @NotBlank
    @Size(max = 50)
    @Email
    private String email;
    private Set<String> role;
    @NotBlank
    @Size(min = 8, max = 120)
    private String password;
}