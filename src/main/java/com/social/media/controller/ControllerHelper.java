package com.social.media.controller;

import org.springframework.security.core.Authentication;

public class ControllerHelper {
    public static String getRole(Authentication authentication) {
        return authentication
                .getAuthorities()
                .stream()
                .findFirst()
                .get()
                .getAuthority()
                .substring(5)
                .toLowerCase();
    }
}
