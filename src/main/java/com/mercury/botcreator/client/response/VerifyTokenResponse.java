package com.mercury.botcreator.client.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class VerifyTokenResponse extends ApiResponseData {

    @JsonProperty("fullname")
    private String fullName;
    private String avatar;
    @JsonProperty("g_id")
    private String gId;
    private String level;
    private String expried;
    @JsonProperty("last_login")
    private String lastLogin;
    @JsonProperty("login_count")
    private int loginCount;
    @JsonProperty("register_ip")
    private String registerIp;
    @JsonProperty("deny_game_ids")
    private List<String> denyGameIds;
    private List<Integer> rooms;
    @JsonProperty("fg_id")
    private String fgId;
    private String ip;
    @JsonProperty("ip_country")
    private String ipCountry;
    private String os;
    private String device;
    private String browser;
    private String status;
    @JsonProperty("is_deposit")
    private boolean isDeposit;
    @JsonProperty("is_bet")
    private boolean isBet;
    @JsonProperty("is_required_captcha")
    private boolean isRequiredCaptcha;
    @JsonProperty("main_balance")
    private long mainBalance;
    @JsonProperty("extra_balance")
    private long extraBalance;
    private long time;
    @JsonProperty("_tmp1")
    private int tmp1;
    @JsonProperty("agency_id")
    private int agencyId;
    @JsonProperty("agency_code")
    private String agencyCode;
    @JsonProperty("agency_code2")
    private String agencyCode2;
    @JsonProperty("member_id")
    private long memberId;
    @JsonProperty("session_id")
    private String sessionId;
    private String token;
    private String type;
    private String uid;
    private String uuid;
    private String username;
    @JsonProperty("user_id")
    private int userId;
    @JsonProperty("u_agent_login")
    private String uAgentLogin;
    private String language;
    private String currency;
    @JsonProperty("created_time")
    private String createdTime;
    @JsonProperty("last_updated_time")
    private String lastUpdatedTime;

}
