package com.mercury.botcreator.util;

import com.mercury.botcreator.model.BotCreationRecord;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@UtilityClass
public class BotCreationReportWriter {

    private static final String REPORT_DIR = "created-bot-reports";
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

    public static void writeReport(String flowType, List<BotCreationRecord> records) {
        try {
            Path dir = Paths.get(REPORT_DIR);
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);
            }

            String fileName = String.format("%s-%s.csv", flowType, LocalDateTime.now().format(TIMESTAMP_FORMAT));
            Path filePath = dir.resolve(fileName);

            try (PrintWriter writer = new PrintWriter(new FileWriter(filePath.toFile()))) {
                writer.println(BotCreationRecord.csvHeader());
                for (BotCreationRecord record : records) {
                    writer.println(record.toCsvRow());
                }
            }

            log.info("Bot creation report saved: {}", filePath);
        } catch (IOException e) {
            log.error("Failed to write bot creation report: {}", e.getMessage());
        }

        logStatistics(flowType, records);
    }

    private static void logStatistics(String flowType, List<BotCreationRecord> records) {
        int total = records.size();
        long registered = records.stream().filter(BotCreationRecord::isRegistered).count();
        long updated = records.stream().filter(BotCreationRecord::isDisplayNameUpdated).count();
        long deposited = records.stream().filter(BotCreationRecord::isDeposited).count();

        log.info("[{}] Bot creation summary - Requested: {}, Registered: {}, DisplayName Updated: {}, Deposited: {}",
                flowType, total, registered, updated, deposited);
    }
}