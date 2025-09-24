package com.example.bankcards.config;


import com.example.bankcards.constants.ApiConstants;
import com.example.bankcards.enums.Role;
import com.example.bankcards.security.JwtFilter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtFilter jwtFilter;

    private final String[] freeResourceUrls = {"/swagger-ui.html",
            "/swagger-ui/**", "/swagger-resources/**",
            "/v3/api-docs/**", "/webjars/**",
            "/api-docs/**", "/aggregate/**"};

    private final String[] onlyAdminUrls = {
            ApiConstants.API_PREFIX_V1.concat(ApiConstants.Account.API_ACCOUNT_ROLE_UPDATE),
            ApiConstants.API_PREFIX_V1.concat(ApiConstants.Account.API_ACCOUNT_LIST),
            ApiConstants.API_PREFIX_V1.concat(ApiConstants.Card.API_CARD_CREATE),
            ApiConstants.API_PREFIX_V1.concat(ApiConstants.Card.API_CARD_STATUS)

    };

    @Bean
    @SneakyThrows
    public SecurityFilterChain securityFilterChain(HttpSecurity http) {
        http
                .cors(conf -> conf.disable())
                .csrf(conf -> conf.disable())
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers(freeResourceUrls).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiConstants.API_PREFIX_V1+ ApiConstants.Authorization.API_AUTHORIZATION_LOGIN).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiConstants.API_PREFIX_V1 + ApiConstants.Authorization.API_AUTHORIZATION_REFRESH).permitAll()
                        .requestMatchers(HttpMethod.POST, ApiConstants.API_PREFIX_V1 + ApiConstants.Account.API_ACCOUNT_CREATE_UPDATE).permitAll()
                        .requestMatchers(HttpMethod.DELETE, ApiConstants.API_PREFIX_V1.concat(ApiConstants.Card.API_CARD_BY_ID.replace("{id}","**"))).hasAnyRole(Role.ADMIN.getRoleWithoutPrefix())
                        .requestMatchers(onlyAdminUrls).hasAnyRole(Role.ADMIN.getRoleWithoutPrefix())
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @SneakyThrows
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) {
        return configuration.getAuthenticationManager();
    }
}