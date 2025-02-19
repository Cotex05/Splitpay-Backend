package com.splitpay.app.payload.dto;

import com.splitpay.app.model.PaymentChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String fullName;
    private String photoUrl;
    private Long userId;
    private String username;
    private String email;
}
