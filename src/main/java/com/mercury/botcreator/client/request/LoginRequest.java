package com.mercury.botcreator.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    @JsonProperty("app_id")
    private String appId;
    private String os;
    private String device;
    private String browser;
    private String fg;


    public static LoginRequest createTemplate(String appId) {
        LoginRequest request = new LoginRequest();
        request.setAppId(appId);
        request.setOs("OS X");
        request.setDevice("Computer");
        request.setBrowser("chrome");
        request.setFg("1d2ffbef3226929d0b1f63373fdc74c8");
        return request;
    }
}
