package com.social.media.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.social.media.model.dto.user.UserCreateRequest;

public class StaticHelperForMVC {
    public static <T> String asJsonString(final T object) {
        try {
            return new ObjectMapper().registerModule(new JavaTimeModule())
                    .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).writeValueAsString(object);
        }catch (Exception exception) {
            throw new RuntimeException();
        }
    }

    public static UserCreateRequest createUser(String username, String firstName, String lastName, String email, String password) {
       return new UserCreateRequest(username, firstName, lastName, email, password);
    }
}
