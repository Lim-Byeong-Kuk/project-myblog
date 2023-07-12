package com.myblog.api.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.myblog.api.domain.Post;
import com.myblog.api.repository.PostRepository;
import com.myblog.api.request.PostCreate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.restdocs.request.RequestDocumentation;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.myblog.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
public class PostControllerDocTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("글 단건 조회 테스트")
    public void search_singlePost() throws Exception {
        //given
        Post post = Post.builder()
                .title("제목 입니다.")
                .content("내용 입니다.")
                .build();
        postRepository.save(post);

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.get("/posts/{postId}", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print())
                .andDo(MockMvcRestDocumentation.document("post-inquiry", pathParameters(
                            parameterWithName("postId").description("게시글 ID")
                        ),
                        responseFields(
                                fieldWithPath("id").description("게시글 ID"),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용")
                        )
                ));
    }

    @Test
    @DisplayName("글 등록")
    public void Post() throws Exception {
        //given
        PostCreate request = PostCreate.builder()
                .title("게시글 제목입니다..")
                .content("내용 입니다.")
                .build();

        String json = objectMapper.writeValueAsString(request);

        //expected
        mockMvc.perform(RestDocumentationRequestBuilders.post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document("post-create",
                        requestFields(
                                fieldWithPath("title").description("게시글 제목")
                                        .attributes(Attributes.key("constraint").value("좋은 제목 입력해주세요.")),
                                fieldWithPath("content").description("게시글 내용").optional()
                        )
                ));
    }
}
