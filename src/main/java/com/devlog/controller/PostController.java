package com.devlog.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devlog.request.PostCreate;
import com.devlog.response.PostResponse;
import com.devlog.service.PostService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping("/posts")
    public void post(@RequestBody @Valid final PostCreate postCreate) {
        postService.save(postCreate);
    }

    @GetMapping("/posts/{postId}")
    public PostResponse findById(@PathVariable final Long postId) {
        return postService.findById(postId);
    }

    @GetMapping("/posts")
    public List<PostResponse> findAll(final Pageable pageable) {
        return postService.findAll(pageable);
    }
}
