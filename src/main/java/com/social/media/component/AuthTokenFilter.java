package com.social.media.component;

import com.social.media.service.UserService;
import com.social.media.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@AllArgsConstructor
public class AuthTokenFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final UserService userService;

    private static final String HEADER_PREFIX = "Bearer ";

    @Override
    public void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response,
                                 @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (hasAuthorizationBearer(request)) {
            String token = getAccessToken(request);
            if (jwtUtils.isJwtTokenValid(token)) {
                setAuthContext(token, request);
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean hasAuthorizationBearer(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        return Objects.nonNull(header) && header.startsWith(HEADER_PREFIX);
    }

    private String getAccessToken(HttpServletRequest request) {
        return request.getHeader("Authorization").substring(HEADER_PREFIX.length());
    }

    private void setAuthContext(String token, HttpServletRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                getUsernamePasswordAuthenticationToken(token);

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
    }

    private UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(String token) {
       var userDetails = userService.getUserBy_Username_Email(jwtUtils.getSubject(token));

        return new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                null, userDetails.getAuthorities());
    }
}
