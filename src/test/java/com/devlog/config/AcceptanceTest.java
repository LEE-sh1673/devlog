package com.devlog.config;

import com.devlog.annotation.CustomSpringBootTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@CustomSpringBootTest
public abstract class AcceptanceTest {

    @Autowired
    private DatabaseCleanUpService databaseCleanUpService;

    @BeforeAll
    void init() {
        databaseCleanUpService.afterPropertiesSet();
    }

    @BeforeEach
    void setUp() {
        databaseCleanUpService.execute();
    }
}
