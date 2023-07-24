package com.social.media.exception;

public class InvalidTextException extends RuntimeException {
    public InvalidTextException() {
    }

    public InvalidTextException(String message) {
        super(message);
    }
}
