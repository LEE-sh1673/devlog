package com.devlog.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.domain.User;
import com.devlog.errors.v2.AlreadyExistsEmailException;
import com.devlog.repository.UserRepository;
import com.devlog.service.dto.SignUpRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder encoder;

    @Transactional
    public void signup(final SignUpRequestDto requestDto) {
        validateCredentials(requestDto.getEmail());
        final User user = modelMapper.map(requestDto, User.class);
        user.updatePassword(encodePassword(requestDto.getPassword()));
        userRepository.save(user);
    }

    private void validateCredentials(final String email) {
        if (isDuplicatedEmail(email)) {
            throw new AlreadyExistsEmailException();
        }
    }

    private boolean isDuplicatedEmail(final String email) {
        return userRepository.existsUserByEmail(email);
    }

    private String encodePassword(final String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
