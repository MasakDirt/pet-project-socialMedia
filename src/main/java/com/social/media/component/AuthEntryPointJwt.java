package com.social.media.component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.info("Content-Type: {}", request.getContentType());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: Unauthorized (please authorize before going to this URL," +
                " go to '/api/auth/login/(username or email)' and authorize there, than return!)");
    }
}
