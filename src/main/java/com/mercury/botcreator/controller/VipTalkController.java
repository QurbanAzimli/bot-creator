package com.mercury.botcreator.controller;

import com.mercury.botcreator.client.request.SendMessageRequest;
import com.mercury.botcreator.service.VipTalkService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/viptalk")
public class VipTalkController {

    private final VipTalkService vipTalkService;

    @PostMapping("/send")
    public String sendMessage(@RequestBody SendMessageRequest request) {
        log.info("Sending message to VipTalk...");
        return vipTalkService.sendMessage(request);
    }

    @Operation(summary = "Send file to VipTalk")
    @PostMapping(value = "/send_file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String sendFile(@RequestParam("file") MultipartFile file) {
        log.info("Sending file to VipTalk: {}", file.getOriginalFilename());
        return vipTalkService.sendFile(file);
    }
}
