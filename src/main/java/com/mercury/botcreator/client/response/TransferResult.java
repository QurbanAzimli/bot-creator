package com.mercury.botcreator.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class TransferResult {
    @JsonProperty("amount")
    private int amount;
    @JsonProperty("amount_before")
    private int amountBefore;
    @JsonProperty("amount_after")
    private int amountAfter;
    @JsonProperty("req_amount")
    private int reqAmount;
    @JsonProperty("dues_amount")
    private int duesAmount;
    @JsonProperty("transaction_id")
    private String transactionId;
    @JsonProperty("agency_transaction_id")
    private String agencyTransactionId;
    @JsonProperty("status")
    private String status;
    @JsonProperty("error_code")
    private int errorCode;
    @JsonProperty("message")
    private String message;
    @JsonProperty("time")
    private long time;
    @JsonProperty("wallets")
    private List<Wallet> wallets;


    @Data
    public static class Wallet {
        @JsonProperty("type")
        private int type;
        @JsonProperty("balance")
        private long balance;
    }
}
