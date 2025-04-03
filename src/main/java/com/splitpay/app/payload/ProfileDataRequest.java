package com.splitpay.app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDataRequest {
    String fullName;
    String email;
    String phone;
    String username;
}
