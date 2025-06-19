package com.mercury.botcreator.client.response;

import lombok.Data;

import java.util.List;

@Data
public class ApiResponse<T extends ApiResponseData> {
    private String status;
    private int code;
    private String message;
    private List<T> data;
}
