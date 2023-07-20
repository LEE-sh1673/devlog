package com.devlog.repository;

import com.devlog.domain.Session;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<Session, Long> {

    Optional<Session> findByAccessToken(final String accessToken);
}
