package com.devlog.controller;

import static com.devlog.utils.ApiUtils.*;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devlog.request.PostCreate;
import com.devlog.request.PostEdit;
import com.devlog.request.PostSearch;
import com.devlog.response.PageResponse;
import com.devlog.response.PostResponse;
import com.devlog.service.PostService;
import com.devlog.utils.ApiUtils;

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
    public ApiUtils.ApiResult<PostResponse> findById(@PathVariable final Long postId) {
        return success(postService.findOne(postId));
    }

    @GetMapping("/posts")
    public ApiUtils.ApiResult<PageResponse> findAll(@ModelAttribute final PostSearch postSearch) {
        return success(postService.findAll(postSearch));
    }

    @PatchMapping("/posts/{postId}")
    public void edit(@PathVariable final Long postId,
        @RequestBody @Valid final PostEdit postEdit) {
        postService.edit(postId, postEdit);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable final Long postId) {
        postService.delete(postId);
    }
}
