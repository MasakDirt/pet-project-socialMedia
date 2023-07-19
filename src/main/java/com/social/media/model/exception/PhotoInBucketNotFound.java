package com.social.media.model.exception;

public class PhotoInBucketNotFound extends RuntimeException{
    public PhotoInBucketNotFound() {
    }

    public PhotoInBucketNotFound(String message) {
        super(message);
    }
}
