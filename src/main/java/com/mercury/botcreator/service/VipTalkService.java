package com.mercury.botcreator.service;

import com.mercury.botcreator.client.VipTalkClient;
import com.mercury.botcreator.client.request.SendMessageRequest;
import com.mercury.botcreator.util.ByteArrayMultipartFile;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class VipTalkService {

    @Value("${application.viptalk.token}")
    private String token;

    @Value("${application.viptalk.room-id}")
    private String roomId;

    private final VipTalkClient vipTalkClient;

    public String sendMessage(String text) {
        log.info("Sending message to VipTalk: text={}, roomId={}", text, roomId);
        Map<String, String> formData = Map.of(
                "text", text,
                "roomIds", roomId
        );
        String response = vipTalkClient.sendMessage(token, formData);
        log.info("VipTalk response: {}", response);
        return response;
    }

    public String sendMessage(SendMessageRequest request) {
        return sendMessage(request.getText());
    }

    public String sendFile(Path filePath) {
        log.info("Sending file to VipTalk: file={}, roomId={}", filePath, roomId);
        try {
            byte[] content = Files.readAllBytes(filePath);
            String fileName = filePath.getFileName().toString();
            MultipartFile multipartFile = new ByteArrayMultipartFile(content, fileName, "text/csv");
            String response = vipTalkClient.sendFile(token, multipartFile, roomId);
            log.info("VipTalk sendFile response: {}", response);
            return response;
        } catch (IOException e) {
            log.error("Failed to send file to VipTalk: {}", e.getMessage());
            throw new RuntimeException("Failed to send file", e);
        }
    }

    public String sendFile(MultipartFile file) {
        log.info("Sending file to VipTalk: file={}, roomId={}", file.getOriginalFilename(), roomId);
        String response = vipTalkClient.sendFile(token, file, roomId);
        log.info("VipTalk sendFile response: {}", response);
        return response;
    }
}
