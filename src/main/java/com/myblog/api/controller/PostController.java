package com.myblog.api.controller;

import com.myblog.api.config.data.UserSession;
import com.myblog.api.request.PostCreate;
import com.myblog.api.request.PostEdit;
import com.myblog.api.request.PostSearch;
import com.myblog.api.response.PostResponse;
import com.myblog.api.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/foo")
    public UserSession foo(UserSession userSession) {
        log.info(">>>{}", userSession.id);
        log.info(">>>{}", userSession.issuedAt);
        log.info(">>>{}", userSession.expiration);
        return userSession;
    }

    /**
     * 인증이 필요없는 페이지
     */
    @GetMapping("/bar")
    public Date bar() {
        Date now = new Date();
        log.info("Date now ={}",now);
        return now;
    }

    @GetMapping("/bar2")
    public String bar2(UserSession userSession) {
        return "인증이 필요한 페이지";
    }

    @PostMapping("/posts")
    public void post(
            @RequestBody @Valid PostCreate request,
            @RequestHeader String authorization) {
        if (authorization.equals("forest")) {
            request.validate();
            postService.write(request);
        }
    }


    /**
     * /posts -> 글 전체 조회 (검색 + 페이징)
     * /posts/{postId}  -> 글 한개만 조회
     */
    @GetMapping("/posts/{postId}")
    public PostResponse get(@PathVariable Long postId) {
        PostResponse response = postService.get(postId);
        return response;
    }

    /**
     * 여러개의 글을 조회 API
     * /posts (GET)
     * Page에 관한 파라미터 PostSearch 로 받기
     *
     */
    @GetMapping("/posts")
    public List<PostResponse> getList(@ModelAttribute PostSearch postSearch) {
        System.out.println("postSearch.getPage() = " + postSearch.getPage());
        System.out.println("postSearch.getSize() = " + postSearch.getSize());
        return postService.getList(postSearch);
    }

    /**
     * 글 수정
     * title, content 를 PostEdit으로 받는다.
     */
    @PatchMapping("/posts/{postId}")
    public void edit(
            @PathVariable Long postId,
            @RequestBody @Valid PostEdit request,
            @RequestHeader String authorization) {
        if ( authorization.equals("forest")) {
            postService.edit(postId, request);
        }
    }

    /**
     * 글 삭제
     *
     */
    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId) {
        postService.delete(postId);
    }
}
