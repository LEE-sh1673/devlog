package com.devlog.test;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    private String author;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID", foreignKey = @ForeignKey(name = "FK_COMMENT_POST"))
    private TestPost testPost;

    @Builder
    public Comment(final String author, final String content) {
        this.author = author;
        this.content = content;
    }

    public void updatePost(final TestPost testPost) {
        this.testPost = testPost;
    }
}
