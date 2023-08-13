package com.devlog.service;

import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.domain.Post;
import com.devlog.domain.PostEditor;
import com.devlog.domain.User;
import com.devlog.errors.v2.NotFoundException;
import com.devlog.errors.v2.UserNotFoundException;
import com.devlog.repository.UserRepository;
import com.devlog.repository.post.PostRepository;
import com.devlog.request.post.PostCreate;
import com.devlog.request.post.PostEdit;
import com.devlog.request.post.PostSearch;
import com.devlog.response.PageResponse;
import com.devlog.response.PostResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final UserRepository userRepository;

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public void save(final Long userId, final PostCreate postCreate) {
        final User user = userRepository.findById(userId)
            .orElseThrow(UserNotFoundException::new);
        final Post post = modelMapper.map(postCreate, Post.class);
        post.updateUser(user);
        postRepository.save(post);
    }

    public PostResponse findOne(final Long postId) {
        Objects.requireNonNull(postId, "postId must be provided");
        return PostResponse.of(findById(postId));
    }

    public PageResponse findAll(final PostSearch postSearch, final Pageable pageable) {
        Page<PostResponse> pages = postRepository.findAll(postSearch, pageable);
        return modelMapper.map(pages, PageResponse.class);
    }

    @Transactional
    public void edit(final Long postId, final PostEdit postEdit) {
        final Post post = findById(postId);
        post.edit(getPostEditor(postEdit, post));
    }

    private PostEditor getPostEditor(final PostEdit postEdit, final Post post) {
        return post.toEditor()
            .title(postEdit.getTitle())
            .content(postEdit.getContent())
            .build();
    }

    public void delete(final Long postId) {
        postRepository.delete(findById(postId));
    }

    private Post findById(final Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(NotFoundException::new);
    }
}
