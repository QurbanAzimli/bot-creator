package com.mercury.botcreator.client;

import com.mercury.botcreator.aspect.stereotype.ValidateApiResponse;
import com.mercury.botcreator.client.request.TransferRequest;
import com.mercury.botcreator.client.response.ApiResponse;
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
        url = "${application.brand.integrations.game-ms.host}",
        configuration = AgencyClientConfig.class
)
public interface TransferClient {

    @PostMapping(value = "${application.brand.integrations.game-ms.path.topUp}", consumes = "application/json")
    @ValidateApiResponse(operation = "TopUp Balance")
    ApiResponse<TransferResponse> topUpBalance(@RequestBody List<TransferRequest> request);

    @GetMapping("${application.brand.integrations.game-ms.path.verifyToken}")
    @ValidateApiResponse(operation = "Verify Token")
    ApiResponse<VerifyTokenResponse> verifyToken(@PathVariable("token") String token);
}
