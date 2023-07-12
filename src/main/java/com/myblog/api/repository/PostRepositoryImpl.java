package com.myblog.api.repository;

import com.myblog.api.domain.Post;
import com.myblog.api.domain.QPost;
import com.myblog.api.request.PostSearch;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public List<Post> getList(PostSearch postSearch) {
        return jpaQueryFactory.selectFrom(QPost.post)
                .limit(postSearch.getSize())
                .offset(postSearch.getOffset()) // 수동으로 페이징 보정 처리
                .orderBy(QPost.post.id.desc())
                .fetch();
    }
}
