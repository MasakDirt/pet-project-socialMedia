package com.social.media.exception;

public class PostCreatedException extends RuntimeException {
    public PostCreatedException() {
    }

    public PostCreatedException(String message) {
        super(message);
    }
}
