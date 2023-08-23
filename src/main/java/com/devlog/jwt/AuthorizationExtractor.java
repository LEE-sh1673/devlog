package com.devlog.jwt;

import java.net.URLDecoder;
import java.util.Optional;
import java.util.regex.Pattern;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthorizationExtractor {

    private static final String AUTHORIZATION_HEADER_TYPE = HttpHeaders.AUTHORIZATION;

    private static final Pattern BEARER = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);

    public static Optional<String> extract(final HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER_TYPE);

        if (Strings.isEmpty(header)) {
            return Optional.empty();
        }
        if (log.isDebugEnabled()) {
            log.debug("Authorization api detected: {}", header);
        }
        return checkMatch(parseHeader(header));
    }

    private static String[] parseHeader(final String header) {
        return URLDecoder.decode(header, UTF_8).split(" ");
    }

    private static Optional<String> checkMatch(final String[] parts) {
        if (parts.length == 2 && BEARER.matcher(parts[0]).matches()) {
            return Optional.ofNullable(parts[1]);
        }
        return Optional.empty();
    }
}
