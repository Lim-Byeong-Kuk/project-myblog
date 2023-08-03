package com.myblog.api.service;

import com.myblog.api.crypto.PasswordEncoder;
import com.myblog.api.crypto.ScryptPasswordEncoder;
import com.myblog.api.domain.User;
import com.myblog.api.exception.AlreadyExistsEmailException;
import com.myblog.api.exception.InvalidSignInInformation;
import com.myblog.api.repository.UserRepository;
import com.myblog.api.request.Login;
import com.myblog.api.request.Signup;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Long signin(Login login) {

        User user = userRepository.findByEmail(login.getEmail())
                .orElseThrow(InvalidSignInInformation::new);

        boolean matches = passwordEncoder.matches(login.getPassword(), user.getPassword());
        if (!matches) {
            throw new InvalidSignInInformation();
        }

        return user.getId();
    }

    public void signup(Signup signup) {
        Optional<User> userOptional = userRepository.findByEmail(signup.getEmail());
        if (userOptional.isPresent()) {
            throw new AlreadyExistsEmailException();
        }

        String encryptedPassword = passwordEncoder.encrypt(signup.getPassword());

        User user = User.builder()
                .name(signup.getName())
                .password(encryptedPassword)
                .email(signup.getEmail())
                .build();

        userRepository.save(user);
    }
}
