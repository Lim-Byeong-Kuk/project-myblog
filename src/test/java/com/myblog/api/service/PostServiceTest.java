package com.myblog.api.service;

import com.myblog.api.domain.Post;
import com.myblog.api.exception.PostNotFound;
import com.myblog.api.repository.PostRepository;
import com.myblog.api.request.PostCreate;
import com.myblog.api.request.PostEdit;
import com.myblog.api.request.PostSearch;
import com.myblog.api.response.PostResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


@SpringBootTest
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private PostRepository postRepository;

    @BeforeEach
    public void clean() {
        postRepository.deleteAll();
    }

    @Test
    @DisplayName("글 작성")
    public void test1() {
        //given
        PostCreate postCreate = PostCreate.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        //when
        postService.write(postCreate);

        //then
        assertThat(postRepository.count()).isEqualTo(1L);

        Post post = postRepository.findAll().get(0);
        assertThat(post.getTitle()).isEqualTo("제목입니다.");
        assertThat(post.getContent()).isEqualTo("내용입니다.");
    }

    @Test
    @DisplayName("글 조회X 시, 예외")
    public void search_post_exception() {
        //given
        Long postId = 1L;

        //then
        assertThatThrownBy(() -> postService.get(1L))
                .isInstanceOf(PostNotFound.class);
    }

    @Test
    @DisplayName("글 1개 조회")
    public void single_search_post() {
        //given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        //when
        PostResponse response = postService.get(post.getId());

        //then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("제목입니다.");
        assertThat(response.getContent()).isEqualTo("내용입니다.");
    }

    @Test
    @DisplayName("글 1개 조회 실패")
    public void single_search_post_FAIL() {
        //given
        Post post = Post.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .build();
        postRepository.save(post);

        //expected
        assertThatThrownBy(() -> postService.get(post.getId() + 1))
                .isInstanceOf(PostNotFound.class);
    }

//    @Test
//    @DisplayName("글 1페이지 조회")
//    public void search_post() {
//        //given
//        List<Post> requestPosts = IntStream.range(1,31)
//                        .mapToObj(i -> Post.builder()
//                                .title("게시글 제목 " + i)
//                                .content("게시글 내용 " + i)
//                                .build())
//                        .collect(Collectors.toList());
//
//        postRepository.saveAll(requestPosts);
//
//        // 직접 Pageable 만들기 - 정렬 설정 등
//        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "id"));
//
//        //when
//        List<PostResponse> posts = postService.getList(pageable);
//
//        //then
//        assertThat(posts.size()).isEqualTo(5);
//        assertThat(posts.get(0).getTitle()).isEqualTo("게시글 제목 30");
//        assertThat(posts.get(4).getTitle()).isEqualTo("게시글 제목 26");
//    }

    @Test
    @DisplayName("글 1페이지 조회_수동페이지 보정")
    public void search_post_getList() {
        //given
        List<Post> requestPosts = IntStream.range(0,20)
                .mapToObj(i -> Post.builder()
                        .title("게시글 제목 " + i)
                        .content("게시글 내용 " + i)
                        .build())
                .collect(Collectors.toList());

        postRepository.saveAll(requestPosts);

        PostSearch postSearch = PostSearch.builder()
//                .page(1)
//                .size(10)
                .build();

        //when
        List<PostResponse> posts = postService.getList(postSearch);

        //then
        assertThat(posts.size()).isEqualTo(5);
        assertThat(posts.get(0).getTitle()).isEqualTo("게시글 제목 19");


        List<Post> result = postRepository.findAll();

        for (Post post : result) {
            System.out.println("post = " + post);
        }
    }

    @Test
    @DisplayName("글 제목 수정")
    public void edit_post_title() {
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

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. +id= " + post.getId()));

        assertThat(changedPost.getTitle()).isEqualTo("게시글 제목 수정 완료");

        System.out.println("changedPost.getTitle(): "+changedPost.getTitle());
        System.out.println("changedPost.getContent(): "+changedPost.getContent());
    }


    @Test
    @DisplayName("글 내용 수정")
    public void edit_post_content() {
        //given
        Post post = Post.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 1")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 수정 완료")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. +id= " + post.getId()));

        assertThat(changedPost.getContent()).isEqualTo("게시글 내용 수정 완료");

        System.out.println("changedPost.getTitle(): "+changedPost.getTitle());
        System.out.println("changedPost.getContent(): "+changedPost.getContent());
    }

    @Test
    @DisplayName("글 내용 수정 - 존재하지 않는 글")
    public void edit_post_content_searchFail() {
        //given
        Post post = Post.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 1")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 수정 완료")
                .build();

        //expected
        assertThatThrownBy(() -> postService.edit(post.getId()+3, postEdit))
                .isInstanceOf(PostNotFound.class);

    }

    @Test
    @DisplayName("글 내용 수정_null값 고려")
    public void edit_post_content_nullTitle() {
        //given
        Post post = Post.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 1")
                .build();

        postRepository.save(post);

        PostEdit postEdit = PostEdit.builder()
                .title(null)
                .content("게시글 내용 수정 완료")
                .build();

        //when
        postService.edit(post.getId(), postEdit);

        //then
        Post changedPost = postRepository.findById(post.getId())
                .orElseThrow(() -> new RuntimeException("글이 존재하지 않습니다. +id= " + post.getId()));

        assertThat(changedPost.getTitle()).isEqualTo("게시글 제목 1");
        assertThat(changedPost.getContent()).isEqualTo("게시글 내용 수정 완료");

        System.out.println("changedPost.getTitle(): "+changedPost.getTitle());
        System.out.println("changedPost.getContent(): "+changedPost.getContent());
    }

    @Test
    @DisplayName("게시글 삭제")
    public void delete() {
        //given
        Post post = Post.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 1")
                .build();

        postRepository.save(post);

        //when
        postService.delete(post.getId());

        //then
        assertThat(postRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("게시글 삭제 - 존재하지 않는 글")
    public void delete_searchFail() {
        //given
        Post post = Post.builder()
                .title("게시글 제목 1")
                .content("게시글 내용 1")
                .build();

        postRepository.save(post);

        //expected
        assertThatThrownBy(() -> postService.delete(post.getId() + 1))
                .isInstanceOf(PostNotFound.class);
    }
}