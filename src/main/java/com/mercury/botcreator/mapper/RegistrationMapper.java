package com.mercury.botcreator.mapper;

import com.mercury.botcreator.client.request.RegistrationRequest;
import com.mercury.botcreator.model.BotCreateRequest;

public class RegistrationMapper {



    public static RegistrationRequest toRequest(BotCreateRequest request) {
        RegistrationRequest registrationRequest = new RegistrationRequest();
        registrationRequest.setUsername(request.getBotNamePrefix() + request.getStartIndex());
        registrationRequest.setPassword(request.getBotPassword());
        registrationRequest.setAppId("default_app_id"); // Replace with actual app ID if needed
        registrationRequest.setFullName("Bot " + request.getBotNamePrefix() + request.getStartIndex());
        registrationRequest.setDevice("default_device"); // Replace with actual device if needed
        registrationRequest.setBrowser("default_browser"); // Replace with actual browser if needed
        registrationRequest.setFg("default_fg"); // Replace with actual fg if needed
        return registrationRequest;

    }
}
