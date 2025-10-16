package com.production.game2048.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Custom exception thrown when a game with a specific ID cannot be found.
 * The @ResponseStatus annotation tells Spring Boot to return a 404 NOT FOUND
 * HTTP status code when this exception is thrown from a @Controller.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String message) {
        super(message);
    }
}