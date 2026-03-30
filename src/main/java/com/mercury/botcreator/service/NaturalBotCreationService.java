package com.mercury.botcreator.service;


import com.mercury.botcreator.client.AgencyClient;
import com.mercury.botcreator.client.TransferClient;
import com.mercury.botcreator.client.request.LoginRequest;
import com.mercury.botcreator.client.request.RegistrationRequest;
import com.mercury.botcreator.client.request.TransferRequest;
import com.mercury.botcreator.client.request.UpdateRequest;
import com.mercury.botcreator.client.response.ApiResponse;
import com.mercury.botcreator.client.response.LoginResponse;
import com.mercury.botcreator.client.response.RegistrationResponse;
import com.mercury.botcreator.client.response.TransferResponse;
import com.mercury.botcreator.client.response.VerifyTokenResponse;
import com.mercury.botcreator.model.BotCreateRequest;
import com.mercury.botcreator.model.BotCreationRecord;
import com.mercury.botcreator.util.BotCreationReportWriter;
import com.mercury.botcreator.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.mercury.botcreator.util.Randomizer.randomString;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaturalBotCreationService {


    @Value("${application.brand.appId}")
    private String appId;
    @Value("${application.brand.agencyId}")
    private int agencyId;
    @Value("${application.common.depositAmount}")
    private long amount;


    private final AgencyClient agencyClient;
    private final TransferClient transferClient;

    public void createBots(BotCreateRequest request) {
        long startTime = System.currentTimeMillis();
        int startIndex = request.getStartIndex();
        int endIndex = request.getEndIndex();
        int batchSize = 50;

        List<BotCreationRecord> records = new CopyOnWriteArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(4);

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
        BotCreationReportWriter.writeReport("natural", records);
    }

    private void processBatch(int startIndex, int endIndex, BotCreateRequest request, List<BotCreationRecord> records) {
        String botNamePrefix = request.getBotNamePrefix();
        String botPassword = request.getBotPassword();

        RegistrationRequest registrationTemplate = RegistrationRequest.createTemplate(appId, randomString(33));
        LoginRequest loginTemplate = LoginRequest.createTemplate(appId);
        UpdateRequest updateTemplate = new UpdateRequest();
        TransferRequest transferRequest = TransferRequest.createTemplate();

        for (int i = startIndex; i < endIndex; i++) {
            String username = botNamePrefix + i;
            BotCreationRecord record = new BotCreationRecord(username);

            record.setRegistered(tryRegister(registrationTemplate, username, botPassword));
            if (!record.isRegistered()) {
                records.add(record);
                continue;
            }

            LoginResponse loginResponse = tryLogin(loginTemplate, username, botPassword);
            if (loginResponse == null) {
                records.add(record);
                continue;
            }

            record.setDisplayNameUpdated(tryUpdateDisplayName(updateTemplate, loginResponse.getSessionId()));
            record.setDeposited(tryDeposit(transferRequest, loginResponse.getToken()));

            records.add(record);
        }
    }

    public void registerBots(BotCreateRequest request) {
        int startIndex = request.getStartIndex();
        int endIndex = request.getEndIndex();
        String botNamePrefix = request.getBotNamePrefix();
        String botPassword = request.getBotPassword();

        RegistrationRequest registrationTemplate = RegistrationRequest.createTemplate(appId, randomString(33));

        for (int i = startIndex; i < endIndex; i++) {
            String username = botNamePrefix + i;
            tryRegister(registrationTemplate, username, botPassword);
        }
    }

    private boolean tryRegister(RegistrationRequest template, String username, String password) {
        try {
            template.setUsername(username);
            template.setPassword(password);
            ApiResponse<RegistrationResponse> response = agencyClient.registerUser(template);
            log.info("Registration response for {}: {}", username, response);
            return true;
        } catch (Exception e) {
            log.error("Could not register bot {}: {}", username, e.getMessage());
            return false;
        }
    }

    private LoginResponse tryLogin(LoginRequest template, String username, String password) {
        try {
            template.setUsername(username);
            template.setPassword(password);
            ApiResponse<LoginResponse> response = agencyClient.login(template);
            log.info("Login response for {}: {}", username, response);
            return response.getData().get(0);
        } catch (Exception e) {
            log.error("Could not login bot {}: {}", username, e.getMessage());
            return null;
        }
    }

    private boolean tryUpdateDisplayName(UpdateRequest template, String sessionToken) {
        try {
            template.setFullName(UsernameGenerator.generate("bot", 10));
            agencyClient.updateUser(sessionToken, template);
            log.info("Display name updated");
            return true;
        } catch (Exception e) {
            log.error("Could not update display name: {}", e.getMessage());
            return false;
        }
    }

    private boolean tryDeposit(TransferRequest template, String loginToken) {
        try {
            ApiResponse<VerifyTokenResponse> verifyResponse = transferClient.verifyToken(loginToken);
            VerifyTokenResponse verified = verifyResponse.getData().get(0);

            template.setToken(verified.getToken());
            template.setUid(verified.getUuid());
            template.setAgencyId(agencyId);
            template.setMemberId(verified.getMemberId());
            template.setAmount(amount);
            template.setTransactionId(UUID.randomUUID().toString());

            ApiResponse<TransferResponse> transferResponse = transferClient.topUpBalance(List.of(template));
            log.info("Deposit completed: {}", transferResponse);
            return true;
        } catch (Exception e) {
            log.error("Could not deposit: {}", e.getMessage());
            return false;
        }
    }
}