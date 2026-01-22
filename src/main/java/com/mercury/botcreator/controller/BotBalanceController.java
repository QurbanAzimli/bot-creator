package com.mercury.botcreator.controller;

import com.mercury.botcreator.service.BotBalanceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bots")
public class BotBalanceController {

    private final BotBalanceService botBalanceService;

    @GetMapping("/balance")
    public String checkBotsBalance() {
        log.info("Checking bots balance...");
        botBalanceService.checkAllBotsBalance();
        return "Balance check completed!";
    }
}
