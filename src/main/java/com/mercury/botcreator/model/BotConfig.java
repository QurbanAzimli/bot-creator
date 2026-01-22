package com.mercury.botcreator.model;

import lombok.Data;

import java.util.List;

@Data
public class BotConfig {
    private List<GameBotConfig> games;
}
