package com.mercury.botcreator.client.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RegistrationRequest {

    private String username;
    private String password;
    @JsonProperty("app_id")
    private String appId;
    @JsonProperty("fullname")
    private String fullName;
    private String os;
    private String device;
    private String browser;
    private String fg;
    private String ip;



    public static RegistrationRequest createTemplate(String appId, String fg) {
        RegistrationRequest template = new RegistrationRequest();
        template.setAppId(appId);
        template.setFullName("_undefined");
        template.setOs("OS X");
        template.setDevice("Computer");
        template.setBrowser("chrome");
        template.setFg(fg);
        template.setIp("129.34.254.27");
        return template;
    }
}
