package com.myblog.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class PostSearch {

    private static final int MAX_SIZE = 2000;

    @Builder.Default
    private Integer page = 1;
    @Builder.Default
    private Integer size = 5;

    private PostSearch(Integer page, Integer size) {
        this.page = ( page != null ? page : 1 );
        this.size = ( size != null ? size : 5 );
    }

    public long getOffset() {
        return (long) (Math.max(1, page) - 1) * Math.min(size, MAX_SIZE);
    }

}
