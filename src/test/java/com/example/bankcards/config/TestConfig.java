package com.example.bankcards.config;

import com.example.bankcards.interfaces.services.AccountService;
import com.example.bankcards.interfaces.services.CardService;
import com.example.bankcards.interfaces.services.JwtService;
import com.example.bankcards.mappers.AccountMapper;
import com.example.bankcards.mappers.CardMapper;
import com.example.bankcards.repository.AccountRepository;
import com.example.bankcards.repository.CardRepository;
import com.example.bankcards.service.JwtServiceImpl;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestConfig {

    @Bean
    public AccountService accountService() {
        return Mockito.mock(AccountService.class);
    }

    @Bean
    public JwtService jwtService() {
        return Mockito.mock(JwtService.class);
    }

    @Bean
    public AccountMapper accountMapper() {
        return Mockito.mock(AccountMapper.class);
    }

    @Bean
    public CardService cardService() {
        return Mockito.mock(CardService.class);
    }

    @Bean
    public CardMapper cardMapper() {
        return Mockito.mock(CardMapper.class);
    }


    @Bean
    public AccountRepository accountRepository() {
        return Mockito.mock(AccountRepository.class);
    }

    @Bean
    public CardRepository cardRepository() {
        return Mockito.mock(CardRepository.class);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable())
//                .authorizeHttpRequests(authz -> authz
//                        .anyRequest().permitAll()
//                )
//                .httpBasic(Customizer.withDefaults());
//
//        return http.build();

        http
                .authorizeHttpRequests((authz) -> authz.anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable);
        return http.build();
    }

}




