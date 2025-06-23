package com.mercury.botcreator.client.request;

import lombok.Data;

@Data
public class AdminDepositRequest {
    private String username;
    private long amount;

    public AdminDepositRequest(long amount) {
        this.amount = amount;
    }
}
