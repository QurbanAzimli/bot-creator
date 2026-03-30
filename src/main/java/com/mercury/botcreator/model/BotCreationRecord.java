package com.mercury.botcreator.model;

import lombok.Data;

@Data
public class BotCreationRecord {
    private final String username;
    private boolean registered;
    private boolean displayNameUpdated;
    private boolean deposited;

    public BotCreationRecord(String username) {
        this.username = username;
    }

    public boolean isFullySuccessful() {
        return registered && displayNameUpdated && deposited;
    }

    public String toCsvRow() {
        return String.join(",", username,
                String.valueOf(registered),
                String.valueOf(displayNameUpdated),
                String.valueOf(deposited));
    }

    public static String csvHeader() {
        return "Username,Registered,DisplayNameUpdated,Deposited";
    }
}