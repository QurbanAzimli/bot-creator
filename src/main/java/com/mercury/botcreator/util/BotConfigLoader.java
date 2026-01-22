package com.mercury.botcreator.util;

import com.mercury.botcreator.model.BotConfig;
import com.mercury.botcreator.model.GameBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class BotConfigLoader {

    @Value("${application.brand.botConfigPath}")
    private String botConfigPath;

    @Bean
    public BotConfig botConfig() {
        log.info("Loading bot configuration from: {}", botConfigPath);

        try {
            Yaml yaml = new Yaml();
            Resource resource = botConfigPath.startsWith("/")
                    ? new FileSystemResource(botConfigPath)
                    : new ClassPathResource(botConfigPath);
            InputStream inputStream = resource.getInputStream();
            Map<String, Object> rawConfig = yaml.load(inputStream);

            return mapToBotConfig(rawConfig);
        } catch (Exception e) {
            log.error("Failed to load bot config: {}", e.getMessage());
            throw new RuntimeException("Failed to load bot configuration", e);
        }
    }

    private BotConfig mapToBotConfig(Map<String, Object> rawConfig) {
        BotConfig botConfig = new BotConfig();
        List<GameBotConfig> games = new ArrayList<>();

        List<Map<String, Map<String, Object>>> rawGames =
                (List<Map<String, Map<String, Object>>>) rawConfig.get("games");

        for (Map<String, Map<String, Object>> gameEntry : rawGames) {
            for (Map.Entry<String, Map<String, Object>> entry : gameEntry.entrySet()) {
                GameBotConfig gameBotConfig = new GameBotConfig();
                gameBotConfig.setGameName(entry.getKey());

                Map<String, Object> values = entry.getValue();
                gameBotConfig.setUsernamePrefix((String) values.get("username-prefix"));
                gameBotConfig.setPassword((String) values.get("password"));
                gameBotConfig.setStartIndex((int) values.get("startIndex"));
                gameBotConfig.setEndIndex((int) values.get("endIndex"));

                games.add(gameBotConfig);
            }
        }

        botConfig.setGames(games);
        return botConfig;
    }
}
