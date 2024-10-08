package com.devlog.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devlog.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

}
