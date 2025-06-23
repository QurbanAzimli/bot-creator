package com.mercury.botcreator.service;


import com.mercury.botcreator.client.AgencyClient;
import com.mercury.botcreator.client.request.AdminDepositRequest;
import com.mercury.botcreator.client.request.RegistrationRequest;
import com.mercury.botcreator.client.request.UpdateRequest;
import com.mercury.botcreator.client.response.ApiResponse;
import com.mercury.botcreator.client.response.RegistrationResponse;
import com.mercury.botcreator.model.BotCreateRequest;
import com.mercury.botcreator.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminSupportedBotCreationService {


    @Value("${application.brand.appId}")
    private String appId;
    @Value("${application.common.depositAmount}")
    private long amount;
    @Value("${application.brand.token}")
    private String token;

    private final AgencyClient agencyClient;

    public void createBots(BotCreateRequest request) {
        long startTime = System.currentTimeMillis();
        int startIndex = request.getStartIndex();
        int endIndex = request.getEndIndex();
        int batchSize = 50;

        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = startIndex; i < endIndex; i += batchSize) {
            int from = i;
            int to = Math.min(i + batchSize, endIndex);

            executor.submit(() -> createBots(from, to, request));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Bot creation interrupted", e);
        }
        long endTime = System.currentTimeMillis();

        log.info("Total process took: {} ms", endTime - startTime);
    }


    public void createBots(int startIndex, int endIndex, BotCreateRequest request) {
        String botNamePrefix = request.getBotNamePrefix();
        String botPassword = request.getBotPassword();

        RegistrationRequest registrationTemplate = RegistrationRequest.createTemplate(appId);
        UpdateRequest updateTemplate = new UpdateRequest();
        AdminDepositRequest adminDepositRequest = new AdminDepositRequest(amount);

        for (int i = startIndex; i < endIndex; i++) {
            String username = botNamePrefix + i;
            try {
                registerBots(registrationTemplate, username, botPassword);
                update(updateTemplate, username);
                adminDeposit(adminDepositRequest, username);
            } catch (Exception e) {
                log.error("Could not finish for process for: {}: Error: {}", username, e.getMessage());
            }

        }
    }

    private void registerBots(RegistrationRequest registrationTemplate, String username, String botPassword) {
        registrationTemplate.setUsername(username);
        registrationTemplate.setPassword(botPassword);

        ApiResponse<RegistrationResponse> response = agencyClient.registerBot(token, registrationTemplate);

        log.info("Registration Response received: {}", response);
    }

    private void update(UpdateRequest updateTemplate, String username) {
        updateTemplate.setFullName(UsernameGenerator.generate("bot", 12));
        updateTemplate.setUsername(username);
        ApiResponse<?> response = agencyClient.updateBot(token, updateTemplate);
        log.info("Update Response received: {}", response);
    }

    public void adminDeposit(AdminDepositRequest adminDepositRequest, String username) {
        adminDepositRequest.setUsername(username);

        ApiResponse<?> response = agencyClient.adminDeposit(token, adminDepositRequest);

        log.info("Transfer response received: {}", response);
    }

}
