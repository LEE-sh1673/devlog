package com.devlog.test;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TestPost {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String content;

    @Builder
    public TestPost(String name, String content) {
        this.name = name;
        this.content = content;
    }

    @OneToMany(mappedBy = "testPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 5)
    private List<Comment> comments = new ArrayList<>();

    public void addComment(final Comment comment) {
        comment.updatePost(this);
        comments.add(comment);
    }
}
