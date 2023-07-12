package com.myblog.api.repository;

import com.myblog.api.domain.Post;
import com.myblog.api.request.PostSearch;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> getList(PostSearch postSearch);
}
