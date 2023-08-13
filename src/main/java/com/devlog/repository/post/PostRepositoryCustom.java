package com.devlog.repository.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.devlog.request.post.PostSearch;
import com.devlog.response.PostResponse;

public interface PostRepositoryCustom {

    Page<PostResponse> findAll(final PostSearch eventSearch, final Pageable pageable);

}
