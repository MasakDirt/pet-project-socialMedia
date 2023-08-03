package com.social.media.exception;

public class LikeAlreadyExistException extends RuntimeException{
    public LikeAlreadyExistException() {
    }

    public LikeAlreadyExistException(String message) {
        super(message);
    }
}
