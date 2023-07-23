package com.devlog.repository;

import com.devlog.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final String email);

    Optional<User> findByEmailAndPassword(final String email, final String password);

    boolean existsUserByEmail(final String email);
}
