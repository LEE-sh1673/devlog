package com.devlog.response;

import com.devlog.domain.User;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TokenResponse {

    private String token;

    private UserResponse user;

    public static TokenResponse of(final String token, final UserResponse userResponse) {
        return new TokenResponse(token, userResponse);
    }
}
