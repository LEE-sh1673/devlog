package com.devlog.annotation;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;

@Component
@Profile("test")
public class TruncateTablesExtension implements AfterEachCallback {

    @PersistenceContext
    private EntityManager entityManager;

    private List<String> tableNames;

    @PostConstruct
    public void extractTableNames() {
        tableNames = entityManager.getMetamodel()
            .getEntities()
            .stream()
            .filter(entityType -> entityType.getJavaType().getAnnotation(Entity.class) != null)
            .map(this::toUnderScoreCamelCase)
            .collect(Collectors.toList());

        System.out.println("tableNames = " + tableNames);
    }

    private String toUnderScoreCamelCase(final EntityType<?> entityType) {
        final String entityName = entityType.getName();
        final StringBuilder tableName = new StringBuilder();

        for (int index = 0; index < entityName.length(); index++) {
            char ch = entityName.charAt(index);
            if (index > 0 && Character.isUpperCase(ch)) {
                tableName.append("_");
            }
            tableName.append(Character.toLowerCase(ch));
        }
        return tableName.toString();
    }

    @Override
    @Transactional
    public void afterEach(ExtensionContext context) throws Exception {
        truncateAllTables();
    }

    private void truncateAllTables() {
        entityManager.flush();
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();

        for (String tableName : tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
            entityManager.createNativeQuery("ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1").executeUpdate();
        }
        entityManager.createNativeQuery("SET FOREIGN_KEY_CHECKS = 0").executeUpdate();
    }
}

