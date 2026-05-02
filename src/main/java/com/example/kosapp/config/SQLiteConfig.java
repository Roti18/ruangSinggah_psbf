package com.example.kosapp.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * SQLiteConfig ensures that JPA repositories are properly scanned.
 * The SQLite dialect is configured via application.properties.
 * This class can be expanded to add custom DataSource beans if needed.
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.example.kosapp.repository")
public class SQLiteConfig {
    // Spring Boot auto-configures the DataSource from application.properties.
    // SQLiteDialect is provided by hibernate-community-dialects.
    // No additional beans are required for basic setup.
}
