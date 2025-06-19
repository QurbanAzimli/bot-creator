package com.mercury.botcreator.controller;

import com.mercury.botcreator.model.BotCreateRequest;
import com.mercury.botcreator.service.BotCreationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bots")
public class BotCreationController {


    private final BotCreationService botCreationService;

    @PostMapping
    public String createBot(@RequestBody BotCreateRequest request) {
        log.info("Creating a new bot...");
        botCreationService.createBots(request);
        return "Bot created successfully!";
    }

}
