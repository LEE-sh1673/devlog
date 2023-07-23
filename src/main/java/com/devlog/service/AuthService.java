package com.devlog.service;

import java.util.Date;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.config.AppConfig;
import com.devlog.crypto.PasswordEncoder;
import com.devlog.domain.User;
import com.devlog.errors.v2.AlreadyExistsEmailException;
import com.devlog.errors.v2.UnauthorizedException;
import com.devlog.repository.UserRepository;
import com.devlog.response.SessionResponse;
import com.devlog.service.dto.SignUpRequestDto;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final Long ACCESS_TOKEN_VALIDITY_SECONDS = 10L;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder encoder;

    private final AppConfig appConfig;

    @Transactional(readOnly = true)
    public SessionResponse login(final String email, final String password) {
        final User user = findByEmail(email);

        if (!matchPassword(password, user.getPassword())) {
            throw new UnauthorizedException();
        }
        return SessionResponse.builder()
            .accessToken(buildJWSToken(user.getId()))
            .build();
    }

    private User findByEmail(final String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(UnauthorizedException::new);
    }

    private boolean matchPassword(final String password, final String encodedPassword) {
        return encoder.matches(password, encodedPassword);
    }

    private String buildJWSToken(final Long userId) {
        return Jwts.builder()
            .setSubject(String.valueOf(userId))
            .signWith(Keys.hmacShaKeyFor(appConfig.getJwtKey()))
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
            .compact();
    }

    public void signup(final SignUpRequestDto requestDto) {
        validateCredentials(requestDto.getEmail());
        final User user = modelMapper.map(requestDto, User.class);
        user.updatePassword(encryptPassword(requestDto.getPassword()));
        userRepository.save(user);
    }

    private String encryptPassword(final String password) {
        return encoder.encrypt(password);
    }

    private void validateCredentials(final String email) {
        if (isDuplicatedEmail(email)) {
            throw new AlreadyExistsEmailException();
        }
    }

    private boolean isDuplicatedEmail(final String email) {
        return userRepository.existsUserByEmail(email);
    }
}
