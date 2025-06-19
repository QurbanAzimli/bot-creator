package com.mercury.botcreator.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UpdateResponse extends ApiResponseData {
    @JsonProperty("fullname")
    private String fullName;
}
