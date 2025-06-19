package com.mercury.botcreator.client;

import com.mercury.botcreator.client.request.TransferRequest;
import com.mercury.botcreator.client.response.TransferResponse;
import com.mercury.botcreator.client.response.VerifyTokenResponse;
import com.mercury.botcreator.config.AgencyClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(
        name = "transferClient",
        url = "${application.env.variables.urls.gamems}",
        configuration = AgencyClientConfig.class
)
public interface TransferClient {

    @PostMapping(value = "/gamems/v1/agency/transfer", consumes = "application/json")
    TransferResponse topUpBalance(@RequestBody List<TransferRequest> request);

    @GetMapping("/gamems/v1/agency/verifytoken/{token}")
    VerifyTokenResponse verifyToken(@PathVariable("token") String token);
}
