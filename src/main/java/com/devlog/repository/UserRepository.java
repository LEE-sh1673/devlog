package com.devlog.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devlog.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(final String email);

    Optional<User> findByEmailAndPassword(final String email, final String password);

    boolean existsUserByEmail(final String email);
}
