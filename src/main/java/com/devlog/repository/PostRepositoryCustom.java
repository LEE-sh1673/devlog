package com.devlog.repository;

import java.util.List;

import com.devlog.domain.Post;

public interface PostRepositoryCustom {

    List<Post> findAll(final int page);
}
