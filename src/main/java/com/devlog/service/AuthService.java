package com.devlog.service;

import com.devlog.domain.User;
import com.devlog.errors.v2.UnauthorizedException;
import com.devlog.repository.UserRepository;
import com.devlog.request.LoginRequest;
import com.devlog.response.SessionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public SessionResponse login(final LoginRequest request) {
        User user = userRepository
            .findByEmailAndPassword(request.getEmail(), request.getPassword())
            .orElseThrow(UnauthorizedException::new);

        return modelMapper.map(user.addSession(), SessionResponse.class);
    }
}
