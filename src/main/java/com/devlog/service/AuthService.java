package com.devlog.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devlog.crypto.PasswordEncoder;
import com.devlog.domain.User;
import com.devlog.errors.v2.AlreadyExistsEmailException;
import com.devlog.repository.UserRepository;
import com.devlog.service.dto.SignUpRequestDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder encoder;

    public void signup(final SignUpRequestDto requestDto) {
        validateCredentials(requestDto.getEmail());
        final User user = modelMapper.map(requestDto, User.class);
        user.updatePassword(encryptPassword(requestDto.getPassword()));
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

    private String encryptPassword(final String password) {
        return encoder.encrypt(password);
    }
}
