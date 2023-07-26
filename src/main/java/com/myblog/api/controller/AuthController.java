package com.myblog.api.controller;

import com.myblog.api.domain.User;
import com.myblog.api.exception.InvalidSignInInformation;
import com.myblog.api.repository.UserRepository;
import com.myblog.api.request.Login;
import com.myblog.api.response.SessionResponse;
import com.myblog.api.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/login")
    public SessionResponse login(@RequestBody Login login) {
        String accessToken = authService.signin(login);
        return new SessionResponse(accessToken);
    }
}
