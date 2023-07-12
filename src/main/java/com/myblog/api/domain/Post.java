package com.myblog.api.domain;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob // 자바에서는 String , DB에서는 Long text 형태로
    private String content;

    @Builder
    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }


    public PostEditor.PostEditorBuilder toEditor() {
        return PostEditor.builder()
                .title(title)
                .content(content);
    }

    public void edit(PostEditor postEditor) {
        title = postEditor.getTitle();
        content = postEditor.getContent();
    }
}
