package com.production.game2048.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application's REST controllers.
 * Catches specified exceptions and formats them into a consistent ApiErrorResponse.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles IllegalArgumentException, typically thrown for invalid input parameters.
     *
     * @param ex The caught exception.
     * @return A ResponseEntity with a 400 Bad Request status and a structured error body.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    // Note: GameNotFoundException is already handled by its @ResponseStatus annotation,
    // so we don't need a specific handler here unless we want to customize the body.

    /**
     * A fallback handler for any other unhandled exceptions.
     * This is a safety net to prevent leaking stack traces to the client.
     *
     * @param ex The caught exception.
     * @return A ResponseEntity with a 500 Internal Server Error status.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGlobalException(Exception ex) {
        ApiErrorResponse errorResponse = new ApiErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later."
        );
        // It's good practice to log the actual exception for debugging purposes.
        // log.error("Unhandled exception caught: ", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}