package com.mercury.botcreator.client.response;

import lombok.Data;

@Data
public class RegistrationResponse {
    private String status;
    private int code;
    private String message;
}
