package com.social.media.exception;

public class PhotoInBucketNotFound extends RuntimeException{
    public PhotoInBucketNotFound() {
    }

    public PhotoInBucketNotFound(String message) {
        super(message);
    }
}
