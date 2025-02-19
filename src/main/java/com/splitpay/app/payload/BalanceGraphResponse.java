package com.splitpay.app.payload;

import com.splitpay.app.payload.dto.UserDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BalanceGraphResponse {
    // Money to be paid by this user
    private UserDTO Payer;

    // Money to be paid out to this user
    private UserDTO Payee;

    // Final Balance amount
    private Double amount;
}
