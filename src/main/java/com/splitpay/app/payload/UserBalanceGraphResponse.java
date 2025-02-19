package com.splitpay.app.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBalanceGraphResponse {
    private List<BalanceGraphResponse> toSend;
    private List<BalanceGraphResponse> toReceive;
}
