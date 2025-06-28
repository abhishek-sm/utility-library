package com.github.utils.exception;

/**
 * Exception thrown by HttpUtils when an error occurs.
 */
public class HttpUtilsException extends RuntimeException {

    /**
     * Creates a new HttpUtilsException with the specified message.
     *
     * @param message the message
     */
    public HttpUtilsException(String message) {
        super(message);
    }

    /**
     * Creates a new HttpUtilsException with the specified message and cause.
     *
     * @param message the message
     * @param cause the cause
     */
    public HttpUtilsException(String message, Throwable cause) {
        super(message, cause);
    }
}
