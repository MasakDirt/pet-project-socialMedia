package com.social.media.exception;

public class PhotoDoesNotExist extends RuntimeException{
    public PhotoDoesNotExist() {
    }

    public PhotoDoesNotExist(String message) {
        super(message);
    }
}
