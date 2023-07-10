package com.devlog.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.domain.Post;
import com.devlog.domain.PostEditor;
import com.devlog.errors.v1.NotFoundException;
import com.devlog.repository.PostRepository;
import com.devlog.request.PostCreate;
import com.devlog.request.PostEdit;
import com.devlog.request.PostSearch;
import com.devlog.response.PostResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public void save(final PostCreate postCreate) {
        postRepository.save(postCreate.toEntity());
    }

    @Transactional(readOnly = true)
    public PostResponse findOne(final Long postId) {
        Objects.requireNonNull(postId, "postId must be provided");
        return new PostResponse(findById(postId));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll(final PostSearch postSearch) {
        return postRepository.findAll(postSearch).stream()
            .map(PostResponse::new)
            .collect(Collectors.toList());
    }

    @Transactional
    public void edit(final Long postId, final PostEdit postEdit) {
        Post post = findById(postId);
        PostEditor postEditor = getPostEditor(postEdit, post);
        post.edit(postEditor);
    }

    private PostEditor getPostEditor(final PostEdit postEdit, final Post post) {
        return post.toEditor()
            .title(postEdit.getTitle())
            .content(postEdit.getContent())
            .build();
    }

    @Transactional
    public void delete(final Long postId) {
        postRepository.delete(findById(postId));
    }

    private Post findById(final Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(NotFoundException::new);
    }
}
