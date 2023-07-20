package com.devlog.test;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TestPostRepository extends JpaRepository<TestPost, Long> {

    @Query("select p from TestPost p join fetch p.comments")
    List<TestPost> findAll();
}
