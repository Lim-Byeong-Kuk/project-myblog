package com.myblog.api.controller;

import com.myblog.api.config.AppConfig;
import com.myblog.api.request.Login;
import com.myblog.api.response.SessionResponse;
import com.myblog.api.service.AuthService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final AppConfig appConfig;

    @PostMapping("/auth/login")
    public SessionResponse login(@RequestBody Login login) {
        Long userId = authService.signin(login);

        SecretKey key = Keys.hmacShaKeyFor(appConfig.getJwtKey());

        Date now = new Date();

        log.info("now date ={}",now);

        String jws = Jwts.builder()
                .setSubject(String.valueOf(userId))
                .signWith(key)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis() + (60*1000L)))
                .compact();

        return new SessionResponse(jws);
    }
}
