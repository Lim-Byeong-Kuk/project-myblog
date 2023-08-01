package com.myblog.api.service;

import com.myblog.api.domain.Session;
import com.myblog.api.domain.User;
import com.myblog.api.exception.InvalidSignInInformation;
import com.myblog.api.repository.UserRepository;
import com.myblog.api.request.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    @Transactional
    public Long signin(Login login) {
        User user = userRepository.findByEmailAndPassword(login.getEmail(), login.getPassword())
                .orElseThrow(() -> new InvalidSignInInformation());
        Session session = user.addSession();

        return user.getId();
    }
}
