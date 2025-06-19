package com.mercury.botcreator.client;


import com.mercury.botcreator.client.request.LoginRequest;
import com.mercury.botcreator.client.request.RegistrationRequest;
import com.mercury.botcreator.client.request.UpdateRequest;
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
        url = "${application.env.variables.urls.apigw}",
        configuration = AgencyClientConfig.class
)
public interface AgencyClient {

    @PostMapping(value = "/user/register.aspx", consumes = "application/json")
    RegistrationResponse registerUser(
            @RequestBody RegistrationRequest request
    );

    @PostMapping(value = "/user/login.aspx", consumes = "application/json")
    LoginResponse login(
            @RequestBody LoginRequest request
    );

    @PostMapping(value = "/user/update.aspx", consumes = "application/json")
    UpdateResponse updateUser(
            @RequestBody UpdateRequest request,
            @RequestHeader("X-Token") String token
    );

}
