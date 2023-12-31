package com.social.media.config;

import com.social.media.component.AuthEntryPointJwt;
import com.social.media.component.AuthTokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@AllArgsConstructor
public class SecurityConfig {
    private final AuthTokenFilter authenticationTokenFilter;
    private final AuthEntryPointJwt authEntryPointJwt;
    private final CorsConfigurationSource configurationSource;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        // setting cors and disable csrf(I have my token)!
        httpSecurity
                .cors(
                        cors -> cors.configurationSource(configurationSource)
                )
                .csrf(AbstractHttpConfigurer::disable);

        // added exception handling and authorize requests!
        httpSecurity
                .exceptionHandling(
                        handling -> handling.authenticationEntryPoint(authEntryPointJwt)
                )
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers("/api/auth/login/email").permitAll()
                                .requestMatchers("/api/auth/login/username").permitAll()
                                .requestMatchers("/api/auth/register").permitAll()
                                .anyRequest()
                                .authenticated()
                );

        // added filter before and build SecurityFilterChain!
        return httpSecurity.addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
