package com.github.utils.src.usage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.github.utils.src.main.LogUtils;
import com.github.utils.src.main.enums.LogLevel;

public class LogUtilsUsageDemo {

    private static final LogUtils.Logger logger = LogUtils.getLogger(LogUtilsUsageDemo.class);

    public static void main(String[] args) throws IOException {
        // Initialize logging system
        setupLogging();

        // Basic logging
        logger.info("Application started");

        // Error logging with exception
        try {
            int result = 10 / 0;
        } catch (Exception e) {
            logger.error("Error in calculation", e);
        }

        // Structured logging
        logger.structured()
                .field("userId", "user123")
                .field("action", "login")
                .field("ipAddress", "192.168.1.100")
                .info("User logged in");

        // Using MDC (Mapped Diagnostic Context)
        LogUtils.MDC.put("requestId", "req-12345");
        LogUtils.MDC.put("sessionId", "sess-67890");

        logger.info("Processing request");

        LogUtils.MDC.clear();

        // Performance logging
        logger.timed("database-query", () -> {
            // Simulate database query
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Detailed performance tracking
        processComplexOperation();

        // Context-scoped logging
        Map<String, Object> orderContext = new HashMap<>();
        orderContext.put("orderId", "ORD-9876");
        orderContext.put("customer", "ACME Corp");
        orderContext.put("amount", 1299.99);

        logger.withFields(orderContext, () -> {
            logger.info("Processing order");
            // Simulate order processing
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            logger.info("Order processed successfully");
        });

        logger.info("Application shutdown");
    }

    private static void processComplexOperation() {
        // Detailed performance tracking
        LogUtils.PerformanceTracker tracker = logger.trackPerformance("complex-operation");

        // Simulate step 1
        try {
            Thread.sleep(30);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        tracker.checkpoint("step1");

        // Simulate step 2
        try {
            Thread.sleep(45);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        tracker.checkpoint("step2");

        // Simulate step 3
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        tracker.checkpoint("step3");

        // Stop and log performance data
        tracker.stop();
    }

    private static void setupLogging() throws IOException {
        // Configure LogUtils
        LogUtils.LogManager logManager = LogUtils.getLogManager();

        // Configure console appender
        LogUtils.ConsoleAppender consoleAppender = new LogUtils.ConsoleAppender(LogLevel.DEBUG);
        logManager.registerAppender("console", consoleAppender);

        // Configure JSON appender with file output
        FileWriter fileWriter = new FileWriter("application.log", true);
        LogUtils.JsonAppender jsonAppender = new LogUtils.JsonAppender(fileWriter, LogLevel.INFO);
        logManager.registerAppender("json", jsonAppender);

        // Set log levels
        logManager.setRootLogLevel(LogLevel.INFO);
        logManager.setLogLevel("com.example.demo", LogLevel.DEBUG);

        // Add filters
        logManager.addFilter("noHealthChecks",
                event -> !event.getMessage().contains("health check"));
    }
}
