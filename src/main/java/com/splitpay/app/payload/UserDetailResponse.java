package com.splitpay.app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserDetailResponse {
    public String username;
    public String email;

    public UserDetailResponse(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
