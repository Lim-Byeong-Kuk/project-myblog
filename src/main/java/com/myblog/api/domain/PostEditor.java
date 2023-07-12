package com.myblog.api.domain;

import lombok.Getter;

@Getter
public class PostEditor {
    private String title;
    private String content;

    private PostEditor(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public static PostEditorBuilder builder() {
        return new PostEditorBuilder();
    }

    public static class PostEditorBuilder {
        private String title;
        private String content;

        PostEditorBuilder() {
        }

        public PostEditorBuilder title(String title) {
            if (title != null) {
                this.title = title;
            }
            return this;
        }

        public PostEditorBuilder content(String content) {
            if (content != null) {
                this.content = content;
            }
            return this;
        }

        public PostEditor build() {
            return new PostEditor(title, content);
        }

        public String toString() {
            return "PostEditor.PostEditorBuilder(title=" + this.title + ", content=" + this.content + ")";
        }
    }
}
