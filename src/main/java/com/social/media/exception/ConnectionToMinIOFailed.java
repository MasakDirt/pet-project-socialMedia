package com.social.media.exception;

public class ConnectionToMinIOFailed extends RuntimeException{
    public ConnectionToMinIOFailed() {
    }

    public ConnectionToMinIOFailed(String message) {
        super(message);
    }
}
