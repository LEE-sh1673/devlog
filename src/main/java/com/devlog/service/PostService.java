package com.devlog.service;

import com.devlog.domain.Post;
import com.devlog.domain.PostEditor;
import com.devlog.errors.v2.NotFoundException;
import com.devlog.repository.PostRepository;
import com.devlog.request.PostCreate;
import com.devlog.request.PostEdit;
import com.devlog.request.PostSearch;
import com.devlog.response.PageResponse;
import com.devlog.response.PostResponse;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public void save(final PostCreate postCreate) {
        postRepository.save(modelMapper.map(postCreate, Post.class));
    }

    @Transactional(readOnly = true)
    public PostResponse findOne(final Long postId) {
        Objects.requireNonNull(postId, "postId must be provided");
        return new PostResponse(findById(postId));
    }

    @Transactional(readOnly = true)
    public PageResponse findAll(final PostSearch postSearch) {
        Page<PostResponse> pages = postRepository.findAll(postSearch);
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

    @Transactional
    public void delete(final Long postId) {
        postRepository.delete(findById(postId));
    }

    private Post findById(final Long postId) {
        return postRepository.findById(postId)
            .orElseThrow(NotFoundException::new);
    }
}
