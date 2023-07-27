package com.social.media.component;

import com.social.media.model.entity.User;
import com.social.media.service.UserService;
import com.social.media.util.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;

import jakarta.servlet.FilterChain;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

@AutoConfigureMockMvc
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class AuthTokenFilterTests {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Spy
    private JwtUtils jwtUtils;

    private final AuthTokenFilter tokenFilter;
    private final UserService userService;

    @Value("${my.inspiration.ms}")
    private long inspirationMs;

    @Autowired
    public AuthTokenFilterTests(AuthTokenFilter authTokenFilter, UserService userService) {
        this.tokenFilter = authTokenFilter;
        this.userService = userService;
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(jwtUtils, "inspirationMs", inspirationMs);
    }

    @Test
    public void test_Injected_Components() {
        assertThat(request).isNotNull();
        assertThat(response).isNotNull();
        assertThat(filterChain).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(jwtUtils).isNotNull();
        assertThat(tokenFilter).isNotNull();
    }

    @Test
    public void test_Valid_doFilterInternal() throws ServletException, IOException {
        String token = jwtUtils.generateTokenFromUsername(getUsername());

        when(jwtUtils.isJwtTokenValid(token)).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        tokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isInstanceOf(UsernamePasswordAuthenticationToken.class);
    }

    @Test
    public void test_Invalid_Inspiration_doFilterInternal() throws ServletException, IOException {
        ReflectionTestUtils.setField(jwtUtils, "inspirationMs", 0L);

        String token = jwtUtils.generateTokenFromUsername(getUsername());

        when(jwtUtils.isJwtTokenValid(token)).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        tokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    public void test_Invalid_Token_doFilterInternal() throws ServletException, IOException {
        String token = "invalidUsername";

        when(jwtUtils.isJwtTokenValid(token)).thenReturn(false);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

        tokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidHeaders")
    public void test_Invalid_Headers_doFilterInternal(String header) throws ServletException, IOException {
        String token = jwtUtils.generateTokenFromUsername(getUsername());

        when(jwtUtils.isJwtTokenValid(token)).thenReturn(true);
        when(request.getHeader(header)).thenReturn(token); // while we're passing 'Authorization' header here
                                                           // we must get false because header prefix don`t contain 'Bearer '.

        tokenFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    private static Stream<String> provideInvalidHeaders() {
        return Stream.of("Content-Type", "Authorization");
    }

    private String getUsername() {
        return userService
                .getAll()
                .stream()
                .findAny()
                .orElse(new User())
                .getUsername();
    }
}
