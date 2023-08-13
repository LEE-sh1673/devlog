package com.devlog.domain;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String content;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();

    @Builder
    Post(final String title, final String content, final User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public PostEditor.PostEditorBuilder toEditor() {
        return PostEditor.builder().title(title).content(content);
    }

    public void addComment(final Comment comment) {
        comment.updatePost(this);
        this.comments.add(comment);
    }

    public void updateUser(final User user) {
        this.user = user;
    }

    public void edit(final PostEditor postEditor) {
        this.title = postEditor.getTitle();
        this.content = postEditor.getContent();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("title", title)
            .append("content", content)
            .build();
    }

    public boolean matchUserId(final Long userId) {
        return this.user.getId().equals(userId);
    }
}
