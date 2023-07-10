package com.devlog.repository;

import java.util.List;

import com.devlog.domain.Post;
import com.devlog.request.PostSearch;

public interface PostRepositoryCustom {

    List<Post> findAll(final PostSearch postSearch);
}
