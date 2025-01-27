package com.example.banking;

import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractContainerBaseTest {

    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

    static {
        POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:latest")
                .withDatabaseName("testdb")
                .withUsername("testuser")
                .withPassword("testpass");

        // Start the container
        POSTGRESQL_CONTAINER.start();
    }
}
