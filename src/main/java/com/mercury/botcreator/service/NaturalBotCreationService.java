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
import com.mercury.botcreator.util.UsernameGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = startIndex; i < endIndex; i += batchSize) {
            int from = i;
            int to = Math.min(i + batchSize, endIndex);

            executor.submit(() -> createBots(from, to, request));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.MINUTES); // adjust timeout as needed
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
        LoginRequest loginTemplate = LoginRequest.createTemplate(appId);
        UpdateRequest updateTemplate = new UpdateRequest();
        TransferRequest transferRequest = TransferRequest.createTemplate();

        for (int i = startIndex; i < endIndex; i++) {
            String username = botNamePrefix + i;
            try {
                registerBots(registrationTemplate, username, botPassword);
                LoginResponse loginResponse = login(loginTemplate, username, botPassword);
                update(updateTemplate, loginResponse.getSessionId());
                VerifyTokenResponse verifyTokenResponse = verifyToken(loginResponse.getToken());
                topUpBalance(transferRequest, verifyTokenResponse);
            } catch (Exception e) {
                log.error("Could not finish for process for: {}: Error: {}", username, e.getMessage());
            }

        }
    }

    private void registerBots(RegistrationRequest registrationTemplate, String username, String botPassword) {
        registrationTemplate.setUsername(username);
        registrationTemplate.setPassword(botPassword);

        ApiResponse<RegistrationResponse> response = agencyClient.registerUser(registrationTemplate);

        log.info("Registration Response received: {}", response);
    }

    private LoginResponse login(LoginRequest loginTemplate, String username, String password) {
        loginTemplate.setUsername(username);
        loginTemplate.setPassword(password);

        ApiResponse<LoginResponse> response = agencyClient.login(loginTemplate);

        log.info("Login Response received: {}", response);

        return response.getData().get(0);
    }

    private void update(UpdateRequest updateTemplate, String token) {
        updateTemplate.setFullName(UsernameGenerator.generate("bot", 10));
        agencyClient.updateUser(token, updateTemplate);
        log.info("Avatar updated");
    }

    private VerifyTokenResponse verifyToken(String token) {
        ApiResponse<VerifyTokenResponse> verifyTokenResponse = transferClient.verifyToken(token);
        log.info("Verify token response received: {}", verifyTokenResponse);
        return verifyTokenResponse.getData().get(0);
    }


    public TransferResponse topUpBalance(TransferRequest transferTemplate, VerifyTokenResponse verifyTokenResponse) {
        transferTemplate.setToken(verifyTokenResponse.getToken());
        transferTemplate.setUid(verifyTokenResponse.getUuid());
        transferTemplate.setAgencyId(agencyId);
        transferTemplate.setMemberId(verifyTokenResponse.getMemberId());
        transferTemplate.setAmount(amount);
        transferTemplate.setTransactionId(UUID.randomUUID().toString());

        ApiResponse<TransferResponse> transferResponse = transferClient.topUpBalance(List.of(transferTemplate));

        log.info("Transfer response received: {}", transferResponse);
        return transferResponse.getData().get(0);
    }


}
