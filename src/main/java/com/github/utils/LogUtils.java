package com.github.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

import com.github.utils.enums.LogLevel;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

/**
 * LogUtils - A comprehensive logging utility library that enhances standard Java logging
 * with structured logging, performance monitoring, routing, filtering, and more.
 */
@UtilityClass
public class LogUtils {

    /**
     * Interface for log appenders/destinations
     */
    public interface LogAppender {
        void append(LogEvent event);
        default boolean isEnabled(LogLevel level) {
            return true;
        }
    }

    /**
     * Log event representing a single log message with all metadata
     */
    @Getter
    public static class LogEvent {
      
        private final String message;
        
        private final LogLevel level;
        
        private final String loggerName;
       
        private final Instant timestamp;
        private final Map<String, Object> fields;
       
        private final Throwable throwable;

        private LogEvent(String message, LogLevel level, String loggerName,
                         Map<String, Object> fields, Throwable throwable) {
            this.message = message;
            this.level = level;
            this.loggerName = loggerName;
            this.timestamp = Instant.now();
            this.fields = new HashMap<>(fields);
            this.throwable = throwable;
        }

        public Map<String, Object> getFields() {
            return new HashMap<>(fields);
        }

    }

    /**
     * Builder for creating log events
     */
    public static class LogEventBuilder {
        private String message;
        private LogLevel level;
        private String loggerName;
        private final Map<String, Object> fields = new HashMap<>();
        private Throwable throwable;

        public LogEventBuilder message(String message) {
            this.message = message;
            return this;
        }

        public LogEventBuilder level(LogLevel level) {
            this.level = level;
            return this;
        }

        public LogEventBuilder logger(String loggerName) {
            this.loggerName = loggerName;
            return this;
        }

        public LogEventBuilder field(String key, Object value) {
            this.fields.put(key, value);
            return this;
        }

        public LogEventBuilder fields(Map<String, Object> fields) {
            this.fields.putAll(fields);
            return this;
        }

        public LogEventBuilder throwable(Throwable throwable) {
            this.throwable = throwable;
            return this;
        }

        public LogEvent build() {
            return new LogEvent(message, level, loggerName, fields, throwable);
        }
    }

    /**
     * Logger class that handles log routing and event creation
     */
    public static class Logger {
        private final String name;
        private final LogManager logManager;
        private final ThreadLocal<Map<String, Object>> contextFields = ThreadLocal.withInitial(HashMap::new);

        Logger(String name, LogManager logManager) {
            this.name = name;
            this.logManager = logManager;
        }

        public void trace(String message) {
            log(LogLevel.TRACE, message, null);
        }

        public void debug(String message) {
            log(LogLevel.DEBUG, message, null);
        }

        public void info(String message) {
            log(LogLevel.INFO, message, null);
        }

        public void warn(String message) {
            log(LogLevel.WARN, message, null);
        }

        public void error(String message) {
            log(LogLevel.ERROR, message, null);
        }

        public void error(String message, Throwable throwable) {
            log(LogLevel.ERROR, message, throwable);
        }

        public void fatal(String message) {
            log(LogLevel.FATAL, message, null);
        }

        public void fatal(String message, Throwable throwable) {
            log(LogLevel.FATAL, message, throwable);
        }

        public void log(LogLevel level, String message, Throwable throwable) {
            if (!logManager.isEnabled(name, level)) {
                return;
            }

            LogEventBuilder builder = new LogEventBuilder()
                    .message(message)
                    .level(level)
                    .logger(name);

            if (throwable != null) {
                builder.throwable(throwable);
            }

            // Add context fields
            builder.fields(contextFields.get());

            logManager.log(builder.build());
        }

        public void withField(String key, Object value, Runnable runnable) {
            Map<String, Object> fields = contextFields.get();
            Object old = fields.put(key, value);
            try {
                runnable.run();
            } finally {
                if (old != null) {
                    fields.put(key, old);
                } else {
                    fields.remove(key);
                }
            }
        }

        public void withFields(Map<String, Object> additionalFields, Runnable runnable) {
            Map<String, Object> fields = contextFields.get();
            Map<String, Object> oldValues = new HashMap<>();

            // Save old values and put new ones
            for (Map.Entry<String, Object> entry : additionalFields.entrySet()) {
                String key = entry.getKey();
                if (fields.containsKey(key)) {
                    oldValues.put(key, fields.get(key));
                }
                fields.put(key, entry.getValue());
            }

            try {
                runnable.run();
            } finally {
                // Restore old values
                for (String key : additionalFields.keySet()) {
                    if (oldValues.containsKey(key)) {
                        fields.put(key, oldValues.get(key));
                    } else {
                        fields.remove(key);
                    }
                }
            }
        }

        /**
         * Creates a structured log entry with custom fields
         */
        public StructuredLogger structured() {
            return new StructuredLogger(this);
        }

        /**
         * Track execution time of a block of code
         * 
         * @param operationName Name of the operation to track
         * @param runnable Code to execute and time
         */
        public void timed(String operationName, Runnable runnable) {
            Instant start = Instant.now();
            try {
                runnable.run();
            } finally {
                Duration duration = Duration.between(start, Instant.now());
                info(String.format("Operation '%s' completed in %d ms",
                        operationName, duration.toMillis()));
            }
        }

        /**
         * Track execution time of a code block that returns a value
         * 
         * @param operationName Name of the operation to track
         * @param supplier Code to execute and time
         * @return The value returned by the supplier
         */
        public <T> T timedSupplier(String operationName, Supplier<T> supplier) {
            Instant start = Instant.now();
            try {
                return supplier.get();
            } finally {
                Duration duration = Duration.between(start, Instant.now());
                info(String.format("Operation '%s' completed in %d ms",
                        operationName, duration.toMillis()));
            }
        }

        /**
         * Track execution time with detailed metrics
         */
        public PerformanceTracker trackPerformance(String operationName) {
            return new PerformanceTracker(this, operationName);
        }
    }

    /**
     * Structured logging helper for building structured log messages
     */
    public static class StructuredLogger {
        private final Logger logger;
        private final Map<String, Object> fields = new HashMap<>();

        StructuredLogger(Logger logger) {
            this.logger = logger;
        }

        public StructuredLogger field(String key, Object value) {
            fields.put(key, value);
            return this;
        }

        public void trace(String message) {
            log(LogLevel.TRACE, message, null);
        }

        public void debug(String message) {
            log(LogLevel.DEBUG, message, null);
        }

        public void info(String message) {
            log(LogLevel.INFO, message, null);
        }

        public void warn(String message) {
            log(LogLevel.WARN, message, null);
        }

        public void error(String message) {
            log(LogLevel.ERROR, message, null);
        }

        public void error(String message, Throwable throwable) {
            log(LogLevel.ERROR, message, throwable);
        }

        public void fatal(String message) {
            log(LogLevel.FATAL, message, null);
        }

        public void fatal(String message, Throwable throwable) {
            log(LogLevel.FATAL, message, throwable);
        }

        private void log(LogLevel level, String message, Throwable throwable) {
            logger.withFields(fields, () -> logger.log(level, message, throwable));
        }
    }

    /**
     * Performance tracking utility for detailed timing metrics
     */
    public static class PerformanceTracker {
        private final Logger logger;
        private final String operationName;
        private final Instant start;
        private Instant lastMark;
        private final Map<String, Duration> checkpoints = new HashMap<>();
        private boolean stopped = false;

        PerformanceTracker(Logger logger, String operationName) {
            this.logger = logger;
            this.operationName = operationName;
            this.start = Instant.now();
            this.lastMark = start;
        }

        /**
         * Mark a checkpoint in the performance tracking
         * 
         * @param checkpointName Name of the checkpoint
         * @return This performance tracker for method chaining
         */
        public PerformanceTracker checkpoint(String checkpointName) {
            if (stopped) {
                return this;
            }

            Instant now = Instant.now();
            Duration sinceLast = Duration.between(lastMark, now);
            checkpoints.put(checkpointName, sinceLast);
            lastMark = now;
            return this;
        }

        /**
         * Stop tracking and log the results
         */
        public void stop() {
            if (stopped) {
                return;
            }

            stopped = true;
            Instant end = Instant.now();
            Duration total = Duration.between(start, end);

            // Log performance data
            logger.withField("operation", operationName, () -> 
                    logger.withField("totalDurationMs", total.toMillis(), () -> {
                for (Map.Entry<String, Duration> entry : checkpoints.entrySet()) {
                    logger.withField("checkpoint." + entry.getKey() + ".ms",
                            entry.getValue().toMillis(), () -> {});
                }
                logger.info("Performance tracking completed for operation: " + operationName);
            }));
        }
    }

    /**
     * Log manager that handles appenders and log routing
     */
    public static class LogManager {
        private final Map<String, LogAppender> appenders = new ConcurrentHashMap<>();
        private final Map<String, LogLevel> loggerLevels = new ConcurrentHashMap<>();
        private LogLevel rootLevel = LogLevel.INFO;
        private final Map<String, Predicate<LogEvent>> filters = new ConcurrentHashMap<>();

        /**
         * Register a log appender
         * 
         * @param name Name of the appender
         * @param appender The appender implementation
         */
        public void registerAppender(String name, LogAppender appender) {
            appenders.put(name, appender);
        }

        /**
         * Set the log level for a specific logger
         * 
         * @param loggerName Logger name or pattern
         * @param level The log level
         */
        public void setLogLevel(String loggerName, LogLevel level) {
            loggerLevels.put(loggerName, level);
        }

        /**
         * Set the root log level
         * 
         * @param level The log level
         */
        public void setRootLogLevel(LogLevel level) {
            rootLevel = level;
        }

        /**
         * Get the effective log level for a logger
         * 
         * @param loggerName Logger name
         * @return The effective log level
         */
        public LogLevel getEffectiveLevel(String loggerName) {
            // Check for exact matches first
            if (loggerLevels.containsKey(loggerName)) {
                return loggerLevels.get(loggerName);
            }

            // Then check for parent packages
            String packageName = loggerName;
            while (packageName.contains(".")) {
                packageName = packageName.substring(0, packageName.lastIndexOf('.'));
                if (loggerLevels.containsKey(packageName)) {
                    return loggerLevels.get(packageName);
                }
            }

            // Finally, return the root level
            return rootLevel;
        }

        /**
         * Check if a specific log level is enabled for a logger
         * 
         * @param loggerName Logger name
         * @param level Log level to check
         * @return True if the level is enabled
         */
        public boolean isEnabled(String loggerName, LogLevel level) {
            LogLevel effectiveLevel = getEffectiveLevel(loggerName);
            return level.getSeverity() >= effectiveLevel.getSeverity();
        }

        /**
         * Add a filter for log events
         * 
         * @param filterName Name of the filter
         * @param filter Predicate to determine if log events should be logged
         */
        public void addFilter(String filterName, Predicate<LogEvent> filter) {
            filters.put(filterName, filter);
        }

        /**
         * Remove a filter
         * 
         * @param filterName Name of the filter to remove
         */
        public void removeFilter(String filterName) {
            filters.remove(filterName);
        }

        /**
         * Log an event to all registered appenders
         * 
         * @param event Log event to log
         */
        public void log(LogEvent event) {
            // Check if this event should be filtered
            for (Predicate<LogEvent> filter : filters.values()) {
                if (!filter.test(event)) {
                    return;
                }
            }

            // Log to all registered appenders that accept this level
            for (LogAppender appender : appenders.values()) {
                if (appender.isEnabled(event.getLevel())) {
                    appender.append(event);
                }
            }
        }
    }
    
    // Singleton instance for the log manager
    @Getter
    private static final LogManager logManager = new LogManager();

    /**
     * Get a logger with a specific name
     * 
     * @param name Logger name (usually the class or package name)
     * @return Logger instance
     */
    public static Logger getLogger(String name) {
        return new Logger(name, logManager);
    }

    /**
     * Get a logger for a specific class
     * 
     * @param clazz Class to get logger for
     * @return Logger instance
     */
    public static Logger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * Console log appender implementation
     */
    @Setter
    public static class ConsoleAppender implements LogAppender {
        private LogLevel threshold = LogLevel.INFO;

        public ConsoleAppender() {
        }

        public ConsoleAppender(LogLevel threshold) {
            this.threshold = threshold;
        }

        @Override
        public void append(LogEvent event) {
            // Simple console output
            StringBuilder builder = new StringBuilder();
            builder.append(event.getTimestamp())
                    .append(" [")
                    .append(event.getLevel())
                    .append("] ")
                    .append(event.getLoggerName())
                    .append(": ")
                    .append(event.getMessage());

            // Add fields if present
            if (!event.getFields().isEmpty()) {
                builder.append(" - ");
                boolean first = true;
                for (Map.Entry<String, Object> entry : event.getFields().entrySet()) {
                    if (!first) {
                        builder.append(", ");
                    }
                    builder.append(entry.getKey()).append("=").append(entry.getValue());
                    first = false;
                }
            }

            System.out.println(builder);

            // Print stack trace if there is a throwable
            if (event.getThrowable() != null) {
                event.getThrowable().printStackTrace(System.out);
            }
        }

        @Override
        public boolean isEnabled(LogLevel level) {
            return level.getSeverity() >= threshold.getSeverity();
        }

    }

    /**
     * JSON log appender for structured logging
     */
    public static class JsonAppender implements LogAppender {
        @Setter
        private LogLevel threshold = LogLevel.INFO;
        private final Appendable output;

        public JsonAppender(Appendable output) {
            this.output = output;
        }

        public JsonAppender(Appendable output, LogLevel threshold) {
            this.output = output;
            this.threshold = threshold;
        }

        @Override
        public void append(LogEvent event) {
            try {
                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"timestamp\":\"").append(event.getTimestamp()).append("\",");
                json.append("\"level\":\"").append(event.getLevel()).append("\",");
                json.append("\"logger\":\"").append(escapeJson(event.getLoggerName())).append("\",");
                json.append("\"message\":\"").append(escapeJson(event.getMessage())).append("\"");

                // Add fields if present
                if (!event.getFields().isEmpty()) {
                    json.append(",\"fields\":{");
                    boolean first = true;
                    for (Map.Entry<String, Object> entry : event.getFields().entrySet()) {
                        if (!first) {
                            json.append(",");
                        }
                        json.append("\"").append(escapeJson(entry.getKey())).append("\":");
                        appendJsonValue(json, entry.getValue());
                        first = false;
                    }
                    json.append("}");
                }

                // Add throwable if present
                if (event.getThrowable() != null) {
                    json.append(",\"throwable\":{");
                    json.append("\"class\":\"").append(escapeJson(event.getThrowable().getClass().getName())).append("\",");
                    json.append("\"message\":\"").append(escapeJson(String.valueOf(event.getThrowable().getMessage()))).append("\"");
                    json.append("}");
                }

                json.append("}");
                output.append(json).append(System.lineSeparator());
            } catch (Exception e) {
                System.err.println("Error writing JSON log: " + e.getMessage());
            }
        }

        private void appendJsonValue(StringBuilder json, Object value) {
            if (value == null) {
                json.append("null");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else {
                json.append("\"").append(escapeJson(value.toString())).append("\"");
            }
        }

        private String escapeJson(String text) {
            if (text == null) {
                return "";
            }
            return text.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
        }

        @Override
        public boolean isEnabled(LogLevel level) {
            return level.getSeverity() >= threshold.getSeverity();
        }

    }

    /**
     * Memory log appender that keeps logs in memory for debugging
     */
    public static class MemoryAppender implements LogAppender {
        private final int maxEntries;
        private final LogEvent[] entries;
        private int currentIndex = 0;
        private int size = 0;

        public MemoryAppender(int maxEntries) {
            this.maxEntries = maxEntries;
            this.entries = new LogEvent[maxEntries];
        }

        @Override
        public synchronized void append(LogEvent event) {
            entries[currentIndex] = event;
            currentIndex = (currentIndex + 1) % maxEntries;
            if (size < maxEntries) {
                size++;
            }
        }

        public synchronized LogEvent[] getEntries() {
            LogEvent[] result = new LogEvent[size];
            for (int i = 0; i < size; i++) {
                result[i] = entries[(currentIndex - size + i + maxEntries) % maxEntries];
            }
            return result;
        }

        public synchronized void clear() {
            currentIndex = 0;
            size = 0;
        }
    }

    /**
     * MDC (Mapped Diagnostic Context) for adding thread-local context to logs
     */
    public static class MDC {
        private static final ThreadLocal<Map<String, Object>> contextMap = ThreadLocal.withInitial(HashMap::new);

        public static void put(String key, Object value) {
            contextMap.get().put(key, value);
        }

        public static Object get(String key) {
            return contextMap.get().get(key);
        }

        public static void remove(String key) {
            contextMap.get().remove(key);
        }

        public static void clear() {
            contextMap.get().clear();
        }

        public static Map<String, Object> getCopyOfContextMap() {
            return new HashMap<>(contextMap.get());
        }
    }

    /**
     * Utility for creating common log filtering predicates
     */
    public static class LogFilters {
        /**
         * Create a filter that only allows specified log levels
         * @param levels Allowed log levels
         * @return Predicate for filtering log events
         */
        public static Predicate<LogEvent> byLevel(LogLevel... levels) {
            return event -> {
                for (LogLevel level : levels) {
                    if (event.getLevel() == level) {
                        return true;
                    }
                }
                return false;
            };
        }

        /**
         * Create a filter that only allows logs from specified loggers
         * @param loggerNames Logger names to allow
         * @return Predicate for filtering log events
         */
        public static Predicate<LogEvent> byLogger(String... loggerNames) {
            return event -> {
                for (String loggerName : loggerNames) {
                    if (event.getLoggerName().startsWith(loggerName)) {
                        return true;
                    }
                }
                return false;
            };
        }

        /**
         * Create a filter that only allows logs containing specified text
         * @param text Text to filter for
         * @return Predicate for filtering log events
         */
        public static Predicate<LogEvent> containsText(String text) {
            return event -> event.getMessage().contains(text);
        }

        /**
         * Create a filter that only allows logs with specified field values
         * @param fieldName Field name to check
         * @param fieldValue Field value to match
         * @return Predicate for filtering log events
         */
        public static Predicate<LogEvent> byField(String fieldName, Object fieldValue) {
            return event -> {
                Object value = event.getFields().get(fieldName);
                return fieldValue.equals(value);
            };
        }
    }

    /**
     * Initialize the logging system with sensible defaults
     */
    public static void initialize() {
        // Set up a console appender by default
        logManager.registerAppender("console", new ConsoleAppender());
    }
}
