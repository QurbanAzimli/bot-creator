package com.mercury.botcreator.client;


import com.mercury.botcreator.aspect.stereotype.ValidateApiResponse;
import com.mercury.botcreator.client.request.AdminDepositRequest;
import com.mercury.botcreator.client.request.LoginRequest;
import com.mercury.botcreator.client.request.RegistrationRequest;
import com.mercury.botcreator.client.request.UpdateRequest;
import com.mercury.botcreator.client.response.ApiResponse;
import com.mercury.botcreator.client.response.LoginResponse;
import com.mercury.botcreator.client.response.RegistrationResponse;
import com.mercury.botcreator.client.response.UpdateResponse;
import com.mercury.botcreator.config.AgencyClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(
        name = "agencyClient",
        url = "${application.brand.integrations.api-gw.host}",
        configuration = AgencyClientConfig.class
)
public interface AgencyClient {

    @PostMapping(value = "${application.brand.integrations.api-gw.path.register}", consumes = "application/json")
    @ValidateApiResponse(operation = "Register User")
    ApiResponse<RegistrationResponse> registerUser(
            @RequestBody RegistrationRequest request
    );

    @PostMapping(value = "${application.brand.integrations.api-gw.path.register-bot}", consumes = "application/json")
    @ValidateApiResponse(operation = "Register User")
    ApiResponse<RegistrationResponse> registerBot(
            @RequestHeader("X-Token") String token,
            @RequestBody RegistrationRequest request
    );

    @PostMapping(value = "${application.brand.integrations.api-gw.path.login}", consumes = "application/json")
    @ValidateApiResponse(operation = "Login User")
    ApiResponse<LoginResponse> login(
            @RequestBody LoginRequest request
    );

    @PostMapping(value = "${application.brand.integrations.api-gw.path.update}", consumes = "application/json")
    @ValidateApiResponse(operation = "Update User")
    ApiResponse<UpdateResponse> updateUser(
            @RequestHeader("X-Token") String token,
            @RequestBody UpdateRequest request
    );

    @PostMapping(value = "${application.brand.integrations.api-gw.path.update-bot}", consumes = "application/json")
    @ValidateApiResponse(operation = "Update User")
    ApiResponse<?> updateBot(
            @RequestHeader("X-Token") String token,
            @RequestBody UpdateRequest request
    );


    @PostMapping(value = "${application.brand.integrations.api-gw.path.deposit}", consumes = "application/json")
    @ValidateApiResponse(operation = "Deposit Balance")
    ApiResponse<?> adminDeposit(
            @RequestHeader("X-Token") String token,
            @RequestBody AdminDepositRequest request
    );
}
