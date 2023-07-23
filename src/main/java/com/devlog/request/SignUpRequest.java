package com.devlog.request;

import com.devlog.service.dto.SignUpRequestDto;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignUpRequest {

    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    private String email;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;

    @NotBlank(message = "이름은 공백일 수 없습니다.")
    private String name;

    public SignUpRequestDto toServiceDto() {
        return new SignUpRequestDto(email, password, name);
    }
}

