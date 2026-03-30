package com.mercury.botcreator.service;


import com.mercury.botcreator.client.AgencyClient;
import com.mercury.botcreator.client.request.AdminDepositRequest;
import com.mercury.botcreator.client.request.RegistrationRequest;
import com.mercury.botcreator.client.request.UpdateRequest;
import com.mercury.botcreator.client.response.ApiResponse;
import com.mercury.botcreator.client.response.RegistrationResponse;
import com.mercury.botcreator.model.BotCreateRequest;
import com.mercury.botcreator.model.BotCreationRecord;
import com.mercury.botcreator.util.BotCreationReportWriter;
import com.mercury.botcreator.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.mercury.botcreator.util.Randomizer.randomString;

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

        List<BotCreationRecord> records = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(10);

        for (int i = startIndex; i < endIndex; i += batchSize) {
            int from = i;
            int to = Math.min(i + batchSize, endIndex);

            executor.submit(() -> processBatch(from, to, request, records));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Bot creation interrupted", e);
        }

        log.info("Total process took: {} ms", System.currentTimeMillis() - startTime);
        BotCreationReportWriter.writeReport("admin", records);
    }

    private void processBatch(int startIndex, int endIndex, BotCreateRequest request, List<BotCreationRecord> records) {
        String botNamePrefix = request.getBotNamePrefix();
        String botPassword = request.getBotPassword();

        RegistrationRequest registrationTemplate = RegistrationRequest.createTemplate(appId, randomString(33));
        UpdateRequest updateTemplate = new UpdateRequest();
        AdminDepositRequest adminDepositRequest = new AdminDepositRequest(amount);

        for (int i = startIndex; i < endIndex; i++) {
            String username = botNamePrefix + i;
            BotCreationRecord record = new BotCreationRecord(username);

            record.setRegistered(tryRegister(registrationTemplate, username, botPassword));
            if (record.isRegistered()) {
                record.setDisplayNameUpdated(tryUpdateDisplayName(updateTemplate, username));
                record.setDeposited(tryDeposit(adminDepositRequest, username));
            }

            records.add(record);
        }
    }

    private boolean tryRegister(RegistrationRequest template, String username, String password) {
        try {
            template.setUsername(username);
            template.setPassword(password);
            ApiResponse<RegistrationResponse> response = agencyClient.registerBot(token, template);
            log.info("Registration response for {}: {}", username, response);
            return true;
        } catch (Exception e) {
            log.error("Could not register bot {}: {}", username, e.getMessage());
            return false;
        }
    }

    private boolean tryUpdateDisplayName(UpdateRequest template, String username) {
        try {
            template.setFullName(UsernameGenerator.generate("bot", 12));
            template.setUsername(username);
            agencyClient.updateBot(token, template);
            log.info("Display name updated for {}", username);
            return true;
        } catch (Exception e) {
            log.error("Could not update display name for {}: {}", username, e.getMessage());
            return false;
        }
    }

    private boolean tryDeposit(AdminDepositRequest template, String username) {
        try {
            template.setUsername(username);
            agencyClient.adminDeposit(token, template);
            log.info("Deposit completed for {}", username);
            return true;
        } catch (Exception e) {
            log.error("Could not deposit for {}: {}", username, e.getMessage());
            return false;
        }
    }
}