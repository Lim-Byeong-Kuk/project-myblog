package com.myblog.api.service;

import com.myblog.api.crypto.PasswordEncoder;
import com.myblog.api.domain.User;
import com.myblog.api.exception.AlreadyExistsEmailException;
import com.myblog.api.exception.InvalidSignInInformation;
import com.myblog.api.repository.UserRepository;
import com.myblog.api.request.Login;
import com.myblog.api.request.Signup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class AuthServiceTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthService authService;
    @Autowired
    private PasswordEncoder encoder;

    @AfterEach
    public void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("회원가입 성공")
    public void signup_success() {
        // given
        Signup signup = Signup.builder()
                .email("abc@gmail.com")
                .password("1234")
                .name("forest")
                .build();

        // when
        authService.signup(signup);

        // then
        assertThat(userRepository.count()).isEqualTo(1L);

        User user = userRepository.findAll().iterator().next();

        assertThat(user.getEmail()).isEqualTo("abc@gmail.com");
        assertThat(encoder.matches("1234",user.getPassword())).isTrue();
        assertThat(user.getName()).isEqualTo("forest");
    }

    @Test
    @DisplayName("회원가입시 중복된 이메일")
    public void signup_duplicate_fail() {
        // given
        User user = User.builder()
                .email("abc@gmail.com")
                .password("1234")
                .name("abc")
                .build();
        userRepository.save(user);

        Signup signup = Signup.builder()
                .email("abc@gmail.com")
                .password("1234")
                .name("forest")
                .build();

        // expected
        assertThatThrownBy(() -> authService.signup(signup))
                .isInstanceOf(AlreadyExistsEmailException.class);
    }

    @Test
    @DisplayName("로그인 성공")
    public void login_success() {
        // given
        String encryptedPassword = encoder.encrypt("1234");

        User user = User.builder()
                .email("abc@gmail.com")
                .password(encryptedPassword)
                .name("abc")
                .build();
        
        userRepository.save(user);


        Login login = Login.builder()
                .email("abc@gmail.com")
                .password("1234")
                .build();


        // when
        Long userId = authService.signin(login);

        // then
        assertThat(userId).isNotNull();
    }

    @Test
    @DisplayName("로그인 비밀번호 틀림")
    public void login_password_fail() {
        // given
        String encryptedPassword = encoder.encrypt("1234");

        User user = User.builder()
                .email("abc@gmail.com")
                .password(encryptedPassword)
                .name("abc")
                .build();

        userRepository.save(user);

        Login login = Login.builder()
                .email("abc@gmail.com")
                .password("5678")
                .build();

        // expected
        assertThatThrownBy(() -> authService.signin(login))
                .isInstanceOf(InvalidSignInInformation.class);
    }
}