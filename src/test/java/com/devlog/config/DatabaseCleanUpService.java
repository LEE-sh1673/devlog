package com.devlog.config;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.transaction.Transactional;

/**
 * 참고:
 * https://tecoble.techcourse.co.kr/post/2020-09-15-test-isolation/
 */
@Service
@Profile("test")
public class DatabaseCleanUpService implements InitializingBean {

    @PersistenceContext
    private EntityManager em;

    private List<String> tableNames;

    @Override
    public void afterPropertiesSet() {
        tableNames = em.getMetamodel().getEntities().stream()
            .filter(e -> e.getJavaType().getAnnotation(Entity.class) != null)
            .map(this::convertToSnakeCase)
            .collect(Collectors.toList());
    }

    private String convertToSnakeCase(final EntityType<?> entityType) {
        if (entityType == null) {
            return null;
        }
        final String regex = "([a-z])([A-Z])";
        final String replacement = "$1_$2";
        return entityType.getName()
            .replaceAll(regex, replacement)
            .toLowerCase(Locale.ROOT);
    }

    @Transactional
    public void execute() {
        em.flush();
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        for (String tableName : tableNames) {

            em.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            em.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN "
                + "id RESTART WITH 1").executeUpdate();
        }
        em.createNativeQuery("SET FOREIGN_KEY_CHECKS = 1").executeUpdate();
    }
}
