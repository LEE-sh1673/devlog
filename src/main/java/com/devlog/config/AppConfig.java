package com.devlog.config;

import java.util.Base64;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("lsh")
public class AppConfig {

    private byte[] jwtKey;

    public void setJwtKey(final String jwtKey) {
        this.jwtKey = Base64.getDecoder().decode(jwtKey);
    }

    public byte[] getJwtKey() {
        return jwtKey;
    }
}
