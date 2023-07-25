package com.social.media.exception;

public class PostCreationException extends RuntimeException {
    public PostCreationException() {
    }

    public PostCreationException(String message) {
        super(message);
    }
}
