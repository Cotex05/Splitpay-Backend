package com.splitpay.app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class APIResponse {
    public APIResponse(String message, Boolean status) {
        this.message = message;
        this.status = status;
    }

    public String message;
    private Boolean status;
}
