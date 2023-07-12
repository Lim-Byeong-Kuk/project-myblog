package com.myblog.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.api.domain.Post;
import com.myblog.api.repository.PostRepository;
import com.myblog.api.request.PostCreate;
import com.myblog.api.request.PostEdit;
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

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void clean() {
        postRepository.deleteAll();
    }


    @Test
    @DisplayName("/posts 요청시 title값은 필수이다.")
    public void testValidation() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.validation.title").value("타이틀을 입력하세요."))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("@ControllerAdvice로 예외처리")
    public void testControllerAdvice() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value("400"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("잘못된 요청입니다."))
                .andExpect(MockMvcResultMatchers.jsonPath("$.validation.title").value("타이틀을 입력하세요."))
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("/posts 요청시 DB에 값이 저장된다.")
    public void testPost() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());

        // then
        long count = postRepository.count();
        assertThat(count).isEqualTo(1L);

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("제목입니다.");
        assertThat(post.getContent()).isEqualTo("내용입니다.");

    }


    @Test
    @DisplayName("글 1개 조회")
    public void single_search_() throws Exception {
        //given
        Post post = Post.builder()
                .title("123456789012345")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        // 클라이언트 요구사항
        // json 응답에서 title값 길이를 최대 10글자로 해주세요.

        //expected ( when + then )
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{postId}",post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(post.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value("1234567890"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value("내용입니다."))
                .andDo(MockMvcResultHandlers.print());

    }


    @Test
    @DisplayName("페이지 조회 - 첫페이지: page=1 이고 page=0 해도 첫페이지 보여준다")
    public void page_search() throws Exception {
        //given
        List<Post> requestPosts = IntStream.range(0,10)
                .mapToObj(i -> Post.builder()
                        .title("게시글 제목 " + i)
                        .content("게시글 내용 " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(requestPosts);

        //expected
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?page=0&size=5000")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", Matchers.is(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].title").value("게시글 제목 9"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].content").value("게시글 내용 9"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("글 제목 수정")
    public void edit_post_title() throws Exception {
        //given
        Post post = Post.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 1")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("게시글 제목 수정 완료")
                .content("게시글 내용 1")
                .build();

        //expected
        // PATCH posts/{postId}
        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/{postId}", post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit))
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("게시글 삭제")
    public void delete_post() throws Exception {
        //given
        Post post = Post.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 1")
                .build();

        postRepository.save(post);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}", post.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회")
    public void search_fail() throws Exception {
        //expected
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{postId}", 1L)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("존재하지 않는 게시글 수정")
    public void edit_fail() throws Exception {
        PostEdit postEdit = PostEdit.builder()
                .title("게시글 제목 수정 완료")
                .content("게시글 내용 1")
                .build();

        //expected
        mockMvc.perform(MockMvcRequestBuilders.patch("/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postEdit)))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("게시물 작성시 제목에 '바보'는 포함될 수 없다.")
    public void testPost_InvalidRequest() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("나는 바보입니다.")
                .content("내용입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(MockMvcRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andDo(MockMvcResultHandlers.print());
    }
}
