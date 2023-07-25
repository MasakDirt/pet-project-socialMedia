package com.social.media.exception;

public class BucketCreationException extends RuntimeException{
    public BucketCreationException() {
    }

    public BucketCreationException(String message) {
        super(message);
    }
}
