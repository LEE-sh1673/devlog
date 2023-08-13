package com.devlog.annotation;

import java.util.List;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringExtension;

/**
 * 참고:
 * https://tecoble.techcourse.co.kr/post/2020-09-15-test-isolation/
 */
@Component
@Profile("test")
public class H2DatabaseCleaner implements AfterEachCallback {

    private static final String TRUNCATE_QUERY = "SELECT Concat('TRUNCATE TABLE ', TABLE_NAME, ' RESTART IDENTITY;') "
        + "AS q FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = SCHEMA()";

    @Override
    public void afterEach(final ExtensionContext context) {
        final JdbcTemplate jdbcTemplate = getJdbcTemplate(context);
        final List<String> truncateQueries = getTruncateQueries(jdbcTemplate);
        truncateTables(jdbcTemplate, truncateQueries);
    }

    private JdbcTemplate getJdbcTemplate(final ExtensionContext context) {
        return SpringExtension
            .getApplicationContext(context)
            .getBean(JdbcTemplate.class);
    }

    private List<String> getTruncateQueries(final JdbcTemplate jdbcTemplate) {
        return jdbcTemplate.queryForList(TRUNCATE_QUERY, String.class);
    }

    private void truncateTables(final JdbcTemplate jdbcTemplate, final List<String> truncateQueries) {
        execute(jdbcTemplate, "SET REFERENTIAL_INTEGRITY FALSE");
        truncateQueries.forEach(v -> execute(jdbcTemplate, v));
        execute(jdbcTemplate, "SET REFERENTIAL_INTEGRITY TRUE");
    }

    private void execute(final JdbcTemplate jdbcTemplate, final String query) {
        jdbcTemplate.execute(query);
    }
}
