package com.devlog.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.errors.NotFoundException;
import com.devlog.repository.PostRepository;
import com.devlog.request.PostCreate;
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
    public PostResponse findById(final Long postId) {
        Objects.requireNonNull(postId, "postId must be provided");

        return postRepository.findById(postId)
            .map(PostResponse::new)
            .orElseThrow(() -> new NotFoundException("Could not found post for " + postId));
    }

    public List<PostResponse> findAll(final Pageable pageable) {
        return postRepository.findAll(1).stream()
            .map(PostResponse::new)
            .collect(Collectors.toList());
    }
}
