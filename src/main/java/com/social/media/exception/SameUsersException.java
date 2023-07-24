package com.social.media.exception;

public class SameUsersException extends RuntimeException{
    public SameUsersException() {
    }

    public SameUsersException(String message) {
        super(message);
    }
}
