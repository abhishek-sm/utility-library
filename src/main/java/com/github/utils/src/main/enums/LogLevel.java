package com.github.utils.src.main.enums;

import lombok.Getter;

/**
 * Log levels enum to define logging severity
 */
@Getter
public enum LogLevel {
    TRACE(100),
    DEBUG(200),
    INFO(300),
    WARN(400),
    ERROR(500),
    FATAL(600);

    private final int severity;

    LogLevel(int severity) {
        this.severity = severity;
    }
    
}
