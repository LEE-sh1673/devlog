package com.devlog.controller;

import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.devlog.request.comment.CommentCreate;
import com.devlog.request.comment.CommentDelete;
import com.devlog.service.CommentService;
import com.devlog.service.dto.CommentCreateDto;
import com.devlog.service.dto.CommentDeleteDto;
import com.devlog.utils.ApiUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import static com.devlog.utils.ApiUtils.success;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final ModelMapper modelMapper;

    @PostMapping("/posts/{postId}/comments")
    public ApiUtils.ApiResult<?> write(
        @PathVariable final Long postId,
        @RequestBody @Valid final CommentCreate commentCreate) {

        commentService.write(postId, modelMapper.map(commentCreate, CommentCreateDto.class));
        return success(true);
    }

    @PostMapping("/comments/{commentId}/delete")
    public ApiUtils.ApiResult<?> delete(
        @PathVariable final Long commentId,
        @RequestBody @Valid final CommentDelete request) {

        commentService.delete(commentId, modelMapper.map(request, CommentDeleteDto.class));
        return success(true);
    }
}
