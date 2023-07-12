package com.devlog.repository;

import org.springframework.data.domain.Page;

import com.devlog.request.PostSearch;
import com.devlog.response.PostResponse;

public interface PostRepositoryCustom {

    Page<PostResponse> findAll(final PostSearch postSearch);
}
