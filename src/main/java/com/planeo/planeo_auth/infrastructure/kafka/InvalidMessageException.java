package com.planeo.planeo_auth.infrastructure.kafka;

public class InvalidMessageException extends RuntimeException {
    public InvalidMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
