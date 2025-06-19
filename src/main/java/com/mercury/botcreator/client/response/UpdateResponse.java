package com.mercury.botcreator.client.response;

import lombok.Data;

@Data
public class UpdateResponse {
    private String status;
    private int code;
    private String message;
    private Object data;
}
