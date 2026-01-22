package com.mercury.botcreator.client;

import com.mercury.botcreator.config.VipTalkClientConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@FeignClient(
        name = "vipTalkClient",
        url = "${application.viptalk.host}",
        configuration = VipTalkClientConfig.class
)
public interface VipTalkClient {

    @PostMapping(value = "${application.viptalk.path.send-message}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String sendMessage(
            @PathVariable("token") String token,
            @RequestBody Map<String, ?> formData
    );

    @PostMapping(value = "${application.viptalk.path.send-file}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    String sendFile(
            @PathVariable("token") String token,
            @RequestPart("file") MultipartFile file,
            @RequestPart("roomIds") String roomIds
    );
}
