package com.mercury.botcreator.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class LoginResponse {
    private String status;
    private int code;
    private String message;
    private List<Data> data;

    @lombok.Data
    public static class Data {
        private String token;
        @JsonProperty("session_id")
        private String sessionId;
        @JsonProperty("fullname")
        private String fullName;
        private String avatar;
        private int id;
        private int extra_balance;
        private int main_balance;
        private int wallet_101;
        private int wallet_102;
        private boolean is_deposit;
        private String level;
        private String username;
        private boolean allow_login;
        private boolean allow_login_other_device;
        private boolean allow_withdraw;
        private boolean allow_withdraw_safe;
        private boolean is_phone_active;
        private String uuid;
        private boolean is_club;
    }
}
