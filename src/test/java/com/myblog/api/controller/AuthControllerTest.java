package com.myblog.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.api.domain.User;
import com.myblog.api.repository.SessionRepository;
import com.myblog.api.repository.UserRepository;
import com.myblog.api.request.Login;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @BeforeEach
    void clean() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("로그인 성공")
    public void testLogin() throws Exception {
        //given
        userRepository.save(User.builder()
                .name("forest")
                .email("abc@gmail.com")
                .password("1234")
                .build());

        // 암호화 알고리즘 참고 : Scrypt, Bcrypt

        //when
        Login login = Login.builder()
                .email("abc@gmail.com")
                .password("1234")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @Transactional
    @DisplayName("로그인 성공 후 세션 1개 생성")
    public void SessionAfterLogin() throws Exception {
        //given
        User user = userRepository.save(User.builder()
                .name("forest")
                .email("abc@gmail.com")
                .password("1234")
                .build());

        //when
        Login login = Login.builder()
                .email("abc@gmail.com")
                .password("1234")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        User loggedInUser = userRepository.findById(user.getId())
                        .orElseThrow(() -> new RuntimeException());


        assertThat(loggedInUser.getSessions().size()).isEqualTo(1L);
    }

    @Test
    @DisplayName("로그인 성공 후 세션 응답")
    public void SessionResponseAfterLogin() throws Exception {
        //given
        User user = userRepository.save(User.builder()
                .name("forest")
                .email("abc@gmail.com")
                .password("1234")
                .build());

        //when
        Login login = Login.builder()
                .email("abc@gmail.com")
                .password("1234")
                .build();

        String json = objectMapper.writeValueAsString(login);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.accessToken", Matchers.notNullValue()))
                .andDo(MockMvcResultHandlers.print());

    }
}