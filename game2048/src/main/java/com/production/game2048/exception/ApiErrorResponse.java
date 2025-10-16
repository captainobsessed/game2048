package com.production.game2048.exception;

import java.time.Instant;

/**
 * A structured error response object for the API.
 * This provides a consistent, machine-readable error format for clients.
 */
public class ApiErrorResponse {

    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    
    public ApiErrorResponse(int status, String error, String message) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
    }

    // Getters
    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
}