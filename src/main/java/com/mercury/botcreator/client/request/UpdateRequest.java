package com.mercury.botcreator.client.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class UpdateRequest {
    @JsonProperty("fullname")
    private String fullName;
    private String username;
}
