package com.myblog.api.service;

import com.myblog.api.domain.Post;
import com.myblog.api.domain.PostEditor;
import com.myblog.api.exception.PostNotFound;
import com.myblog.api.repository.PostRepository;
import com.myblog.api.request.PostCreate;
import com.myblog.api.request.PostEdit;
import com.myblog.api.request.PostSearch;
import com.myblog.api.response.PostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public void write(PostCreate postCreate) {
        // postCreate -> Entity
        Post post = Post.builder()
                .title(postCreate.getTitle())
                .content(postCreate.getContent())
                .build();

        postRepository.save(post);
    }

    public PostResponse get(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFound());
        // 글이 없을시 예외 던짐

        PostResponse response = PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        return response;
    }


    public List<PostResponse> getList(PostSearch postSearch) {
        return postRepository.getList(postSearch).stream()
                .map(post -> new PostResponse(post))
                .collect(Collectors.toList());
    }

    // 변경된 내용을 응답으로 달라고 할 때도 있음 그러면 PostResponse 으로 리턴하면 됨
    @Transactional
    public void edit(Long id, PostEdit postEdit) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFound());


        PostEditor.PostEditorBuilder editorBuilder = post.toEditor();

        PostEditor postEditor = editorBuilder.title(postEdit.getTitle())
                .content(postEdit.getContent())
                .build();

        post.edit(postEditor);
    }
    
    public void delete(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFound());

        postRepository.delete(post);
    }
}
