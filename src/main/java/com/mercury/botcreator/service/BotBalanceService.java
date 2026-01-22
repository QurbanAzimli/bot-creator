package com.mercury.botcreator.service;

import com.mercury.botcreator.client.AgencyClient;
import com.mercury.botcreator.client.request.LoginRequest;
import com.mercury.botcreator.client.response.ApiResponse;
import com.mercury.botcreator.client.response.LoginResponse;
import com.mercury.botcreator.model.BotConfig;
import com.mercury.botcreator.model.GameBotConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class BotBalanceService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final ExecutorService executor;
    private final ScheduledExecutorService scheduler;
    private final AgencyClient agencyClient;
    private final BotConfig botConfig;
    private final VipTalkService vipTalkService;
    private final String appId;
    private final String reportDir;
    private final long fileSendDelaySeconds;
    private final long requestDelayMs;

    public BotBalanceService(AgencyClient agencyClient, BotConfig botConfig,
                             VipTalkService vipTalkService,
                             @Value("${application.brand.appId}") String appId,
                             @Value("${application.brand.reportDir}") String reportDir,
                             @Value("${application.brand.fileSendDelaySeconds:30}") long fileSendDelaySeconds,
                             @Value("${application.brand.requestDelayMs:200}") long requestDelayMs,
                             @Value("${application.brand.maxConcurrentRequests:3}") int maxConcurrentRequests) {
        this.agencyClient = agencyClient;
        this.botConfig = botConfig;
        this.vipTalkService = vipTalkService;
        this.appId = appId;
        this.reportDir = reportDir;
        this.fileSendDelaySeconds = fileSendDelaySeconds;
        this.requestDelayMs = requestDelayMs;

        this.executor = Executors.newFixedThreadPool(maxConcurrentRequests);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        log.info("BotBalanceService initialized with {} threads, reportDir: {}, fileSendDelay: {}s, requestDelay: {}ms",
                maxConcurrentRequests, reportDir, fileSendDelaySeconds, requestDelayMs);
    }

    @Scheduled(cron = "${application.brand.scheduler.balanceCheck}")
    public void scheduledBalanceCheck() {
        log.info("Scheduled balance check triggered");
        checkAllBotsBalance();
    }

    public void checkAllBotsBalance() {
        log.info("Starting balance check for all configured bots with {}ms delay between requests", requestDelayMs);
        long startTime = System.currentTimeMillis();

        Map<String, List<BalanceRecord>> gameBalances = new ConcurrentHashMap<>();
        List<Future<?>> futures = new ArrayList<>();

        collectFutures(gameBalances, futures);

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("Error waiting for task: {}", e.getMessage());
            }
        }

        long checkTime = System.currentTimeMillis();
        log.info("All balance checks completed in {} ms. Writing reports...", checkTime - startTime);

        createReportDirectory();
        List<Path> generatedFiles = new ArrayList<>();
        for (Map.Entry<String, List<BalanceRecord>> entry : gameBalances.entrySet()) {
            Path filePath = writeBalanceReport(entry.getKey(), entry.getValue());
            if (filePath != null) {
                generatedFiles.add(filePath);
            }
        }

        log.info("Scheduling {} report files to VipTalk with {}s delay between each...",
                generatedFiles.size(), fileSendDelaySeconds);

        for (int i = 0; i < generatedFiles.size(); i++) {
            Path filePath = generatedFiles.get(i);
            long delay = i * fileSendDelaySeconds;

            scheduler.schedule(() -> {
                try {
                    log.info("Sending file to VipTalk: {}", filePath.getFileName());
                    vipTalkService.sendFile(filePath);
                    log.info("Successfully sent file: {}", filePath.getFileName());
                } catch (Exception e) {
                    log.error("Failed to send file {}: {}", filePath.getFileName(), e.getMessage());
                }
            }, delay, TimeUnit.SECONDS);

            log.info("Scheduled file {} to be sent in {}s", filePath.getFileName(), delay);
        }

        long endTime = System.currentTimeMillis();
        log.info("Balance check and report generation completed in {} ms. Files scheduled for sending.", endTime - startTime);
    }

    private void collectFutures(Map<String, List<BalanceRecord>> gameBalances, List<Future<?>> futures) {
        for (GameBotConfig game : botConfig.getGames()) {
            gameBalances.put(game.getGameName(), new CopyOnWriteArrayList<>());

            for (int i = game.getStartIndex(); i <= game.getEndIndex(); i++) {
                String username = game.getUsernamePrefix() + i;
                String gameName = game.getGameName();
                String password = game.getPassword();
                int index = i;

                Future<?> future = executor.submit(() -> {
                    BalanceRecord record = checkBotBalance(username, password, index);
                    gameBalances.get(gameName).add(record);
                });
                futures.add(future);

                // Add delay between submitting requests to avoid rate limiting
                try {
                    Thread.sleep(requestDelayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("Balance check interrupted");
                    break;
                }
            }
        }
    }

    private BalanceRecord checkBotBalance(String username, String password, int index) {
        LoginRequest loginRequest = LoginRequest.createTemplate(appId);
        loginRequest.setUsername(username);
        loginRequest.setPassword(password);

        try {
            ApiResponse<LoginResponse> response = agencyClient.login(loginRequest);
            LoginResponse loginResponse = response.getData().get(0);

            log.info("Bot [{}] - Main Balance: {}, Extra Balance: {}",
                    username,
                    loginResponse.getMain_balance(),
                    loginResponse.getExtra_balance());

            return new BalanceRecord(
                    index,
                    username,
                    loginResponse.getMain_balance(),
                    loginResponse.getExtra_balance(),
                    null
            );
        } catch (Exception e) {
            log.error("Could not check balance for bot: {}: Error: {}", username, e.getMessage());
            return new BalanceRecord(index, username, 0, 0, e.getMessage());
        }
    }

    private void createReportDirectory() {
        try {
            Path reportPath = Paths.get(reportDir);
            if (!Files.exists(reportPath)) {
                Files.createDirectories(reportPath);
                log.info("Created report directory: {}", reportDir);
            }
        } catch (IOException e) {
            log.error("Failed to create report directory: {}", e.getMessage());
            throw new RuntimeException("Failed to create report directory", e);
        }
    }

    private Path writeBalanceReport(String gameName, List<BalanceRecord> records) {
        String fileName = String.format("%s-%s.csv", gameName, LocalDate.now().format(DATE_FORMATTER));
        Path filePath = Paths.get(reportDir, fileName);

        List<BalanceRecord> sortedRecords = new ArrayList<>(records);
        sortedRecords.sort(Comparator.comparingInt(BalanceRecord::index));

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
            writer.println("Username,Main Balance,Extra Balance,Status");

            for (BalanceRecord record : sortedRecords) {
                String status = record.error() == null ? "OK" : record.error();
                writer.printf("%s,%d,%d,%s%n",
                        record.username(),
                        record.mainBalance(),
                        record.extraBalance(),
                        status);
            }

            log.info("Balance report saved: {} ({} records)", filePath, sortedRecords.size());
            return filePath;
        } catch (IOException e) {
            log.error("Failed to write balance report for game {}: {}", gameName, e.getMessage());
            return null;
        }
    }

    private record BalanceRecord(int index, String username, int mainBalance, int extraBalance, String error) {}
}
