package com.mercury.botcreator.controller;

import com.mercury.botcreator.model.BotCreateRequest;
import com.mercury.botcreator.service.AdminSupportedBotCreationService;
import com.mercury.botcreator.service.NaturalBotCreationService;
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


    private final NaturalBotCreationService naturalBotCreationService;
    private final AdminSupportedBotCreationService adminSupportedBotCreationService;

    @PostMapping("/natural")
    public String createBotNatural(@RequestBody BotCreateRequest request) {
        log.info("Creating a new bot...");
        naturalBotCreationService.createBots(request);
        return "Bot created successfully!";
    }

    @PostMapping("/admin")
    public String createBotAdmin(@RequestBody BotCreateRequest request) {
        log.info("Creating a new bot...");
        adminSupportedBotCreationService.createBots(request);
        return "Bot created successfully!";
    }
}
