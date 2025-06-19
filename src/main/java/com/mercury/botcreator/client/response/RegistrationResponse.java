package com.mercury.botcreator.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegistrationResponse extends ApiResponseData {
    private String level;
    @JsonProperty("session_id")
    private String sessionId;
    private String token;
    @JsonProperty("fullname")
    private String fullName;
    private String username;
    @JsonProperty("is_deposit")
    private boolean isDeposit;
}
