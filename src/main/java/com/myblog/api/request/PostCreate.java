package com.myblog.api.request;

import com.myblog.api.exception.InvalidRequest;
import lombok.*;

import javax.validation.constraints.NotBlank;

@ToString
@Setter @Getter
public class PostCreate {

    @NotBlank(message = "타이틀을 입력하세요.")
    private String title;

    @NotBlank(message = "컨텐츠를 입력하세요.")
    private String content;

    public PostCreate() {
    }

    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validate() {
        if (title.contains("바보")) {
            throw new InvalidRequest("title", "제목에 바보를 포함할 수 없습니다.");
        }
    }
}
