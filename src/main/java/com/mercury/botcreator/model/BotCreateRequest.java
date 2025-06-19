package com.mercury.botcreator.model;

import lombok.Data;

@Data
public class BotCreateRequest {

    private int startIndex;
    private int endIndex;
    private String botNamePrefix;
    private String botPassword;

}
