package com.mercury.botcreator.model;

import lombok.Data;

@Data
public class GameBotConfig {
    private String gameName;
    private String usernamePrefix;
    private String password;
    private int startIndex;
    private int endIndex;
}
