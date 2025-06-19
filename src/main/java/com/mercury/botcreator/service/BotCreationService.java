package com.mercury.botcreator.service;


import com.mercury.botcreator.client.AgencyClient;
import com.mercury.botcreator.client.TransferClient;
import com.mercury.botcreator.client.request.LoginRequest;
import com.mercury.botcreator.client.request.RegistrationRequest;
import com.mercury.botcreator.client.request.TransferRequest;
import com.mercury.botcreator.client.request.UpdateRequest;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class BotCreationService {


    @Value("${application.env.variables.deposit-amount}")
    private String appId;
    @Value("${application.env.variables.deposit-amount}")
    private int agencyId;
    @Value("${application.env.variables.deposit-amount}")
    private long amount;


    private final AgencyClient agencyClient;
    private final TransferClient transferClient;


    public void createBots(BotCreateRequest request) {

        int startIndex = request.getStartIndex();
        int endIndex = request.getEndIndex();
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
                update(updateTemplate, loginResponse.getData().get(0).getSessionId());
                VerifyTokenResponse verifyTokenResponse = verifyToken(loginResponse.getData().get(0).getToken());
                topUpBalance(transferRequest, verifyTokenResponse);
            }catch (Exception e) {
                log.error("Could not finish for process for: {}", username);
            }

        }
    }

    private void registerBots(RegistrationRequest registrationTemplate, String username, String botPassword) {
        registrationTemplate.setUsername(username);
        registrationTemplate.setPassword(botPassword);

        RegistrationResponse response = agencyClient.registerUser(registrationTemplate);

        log.info("Registration Response received: {}", response);
    }

    private LoginResponse login(LoginRequest loginTemplate, String username, String password) {
        loginTemplate.setUsername(username);
        loginTemplate.setPassword(password);

        LoginResponse response = agencyClient.login(loginTemplate);

        log.info("Login Response received: {}", response);

        return response;
    }

    private void update(UpdateRequest updateTemplate, String token) {
        updateTemplate.setFullName(UsernameGenerator.generate("bot", 10));
        agencyClient.updateUser(updateTemplate, token);
        log.info("Avatar updated");
    }

    private VerifyTokenResponse verifyToken(String token) {
        VerifyTokenResponse verifyTokenResponse = transferClient.verifyToken(token);
        log.info("Verify token response received: {}", verifyTokenResponse);
        return verifyTokenResponse;
    }


    public TransferResponse topUpBalance(TransferRequest transferTemplate, VerifyTokenResponse verifyTokenResponse) {
        transferTemplate.setToken(verifyTokenResponse.getData().get(0).getToken());
        transferTemplate.setUid(verifyTokenResponse.getData().get(0).getUuid());
        transferTemplate.setAgencyId(agencyId);
        transferTemplate.setMemberId(verifyTokenResponse.getData().get(0).getMemberId());
        transferTemplate.setAmount(amount);
        transferTemplate.setTransactionId(UUID.randomUUID().toString());

        TransferResponse transferResponse = transferClient.topUpBalance(List.of(transferTemplate));

        log.info("Transfer response received: {}", transferResponse);
        return transferResponse;
    }



}
