package com.mercury.botcreator.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransferRequest {
    private String token;
    private String uid;
    @JsonProperty("agency_id")
    private int agencyId;
    @JsonProperty("member_id")
    private long memberId;
    private long amount;
    @JsonProperty("transaction_id")
    private String transactionId;
    private String action;
    private TransferGameData data;


    @Data
    public static class TransferGameData {
        @JsonProperty("game_ticket_status")
        private String gameTicketStatus;
        @JsonProperty("game_your_bet")
        private String gameYourBet;
        @JsonProperty("game_winlost")
        private int gameWinLost;
    }


    public static TransferRequest createTemplate() {
        TransferGameData gameData = new TransferGameData();
        gameData.setGameTicketStatus("Deposit");
        gameData.setGameYourBet("User For BE to test");
        gameData.setGameWinLost(10);

        TransferRequest req = new TransferRequest();
        req.setAction("WIN");
        req.setData(gameData);
        return req;
    }
}
