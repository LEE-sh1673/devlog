package com.devlog.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(
    indexes = {
        @Index(name = "IDX_COMMENT_POST_ID",
            columnList = "post_id")
    }
)
@NoArgsConstructor(access = PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Comment(String author, String password, String content) {
        this.author = author;
        this.password = password;
        this.content = content;
    }

    public void updatePost(final Post post) {
        this.post = post;
    }

    public void updatePassword(final String password) {
        this.password = password;
    }
}
