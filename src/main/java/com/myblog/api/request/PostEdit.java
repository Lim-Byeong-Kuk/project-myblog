package com.myblog.api.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class PostEdit {

    @NotBlank(message = "타이틀을 입력하세요.")
    private String title;

    @NotBlank(message = "컨텐츠를 입력하세요.")
    private String content;

    public PostEdit() {
    }

    @Builder
    public PostEdit(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
