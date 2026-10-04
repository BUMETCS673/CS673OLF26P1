// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 90%
// AI-Assisted Areas: Testcontainer singleton lifecycle setup, Spring Boot dynamic property registration
// Human Contributions: Custom DB configuration, test profile verification, container customization
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.common.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.mysql.MySQLContainer;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create an abstract integration test base class managing a shared MySQL 8.4 Testcontainer instance."
// AI Contribution: Initial draft (~90%)
// Verification: Integration test execution via JUnit 5
// Confidence: High
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public abstract class AbstractIntegrationTest {

    // Static instance started manually in a static block guarantees a single container instance
    // shared across ALL child test classes for the entire JVM lifetime.
//    @SuppressWarnings("resource") // Suppresses false positive IDE warning for unclosed AutoCloseable
    static final MySQLContainer MYSQL_CONTAINER = new MySQLContainer("mysql:8.4");
/*
        .withDatabaseName("testdb")
        .withUsername("testuser")
        .withPassword("testpass");
*/

    static {
        MYSQL_CONTAINER.start();
    }

    // AI-ASSISTED: YES
    // Tool: Gemini
    // Prompt Summary: "Dynamically register Testcontainer database properties for Spring Boot."
    // AI Contribution: Initial draft (~95%)
    // Verification: Spring ApplicationContext boot verification
    // Confidence: High
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);
    }
}