// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 85%
// AI-Assisted Areas: SQL join queries matching RBAC schema, JPA repository verification, Flyway assertions
// Human Contributions: Schema entity alignment, custom role assertion adjustments, validation against application DB
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.integration;

import edu.bu.metcs673.bluejay.auth.entity.User;
import edu.bu.metcs673.bluejay.auth.repository.UserRepository;
import edu.bu.metcs673.bluejay.common.integration.AbstractIntegrationTest;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Integration test class verifying Flyway seeding for multiple default user accounts and RBAC roles."
// AI Contribution: Initial draft (~85%)
// Verification: Execute JUnit 5 test suite via Gradle/Maven test task
// Confidence: High
@DisplayName("User Seeding Integration Tests")
class UserSeedingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Flyway flyway;

    @Nested
    @DisplayName("Flyway Migration Verification")
    class FlywayVerification {

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "Verify Flyway applied migration scripts V1 and V2 successfully."
        // AI Contribution: Initial draft (~90%)
        // Verification: Flyway migration history log assertion
        // Confidence: High
        @Test
        @DisplayName("Flyway applies core schema and seed migrations successfully")
        void flywayMigrationsAppliedSuccessfully() {
            assertThat(flyway.info().applied())
                .extracting(info -> info.getVersion().getVersion())
                .contains("1", "2");
        }
    }

    @Nested
    @DisplayName("Database State & User Seeding")
    class DatabaseState {

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "Verify seeded default users exist with mapped roles via SQL join without breaking on future migrations."
        // AI Contribution: Initial draft (~90%)
        // Modifications: Loosened original test to use specific seeded users.
        // Verification: Parameterized SQL execution via JdbcTemplate
        // Confidence: High
        @ParameterizedTest(name = "User ''{0}'' should be seeded with role ''{1}''")
        @CsvSource({
            "admin, ROLE_ADMIN",
            "manager, ROLE_MANAGER",
            "cashier, ROLE_CASHIER"
        })
        @DisplayName("Scenario 1: Default seeded users exist with correct mapped roles via SQL join")
        void seededUsersShouldBePresentWithCorrectRoles(String username, String expectedRole) {
            String sql = """
                SELECT u.username, r.name AS role_name
                FROM users u
                JOIN user_roles ur ON u.id = ur.user_id
                JOIN roles r ON ur.role_id = r.id
                WHERE u.username = ?
                """;

            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, username);

            assertThat(rows).isNotEmpty();
            assertThat(rows.getFirst().get("username")).isEqualTo(username);
            assertThat(rows.getFirst().get("role_name")).isEqualTo(expectedRole);
        }

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "Assert admin user exists exactly once to verify migration idempotency regardless of future seed data."
        // AI Contribution: Initial draft (~90%)
        // Verification: SQL aggregate query targeting specific admin username
        // Confidence: High
        @Test
        @DisplayName("Scenario 2: Admin user account is unique and non-duplicated")
        void adminUserAccountShouldBeUnique() {
            Integer adminCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE username = ?",
                Integer.class,
                "admin"
            );

            assertThat(adminCount).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("JPA Repository Mapping")
    class JpaMapping {

        // AI-ASSISTED: YES
        // Tool: Gemini
        // Prompt Summary: "Verify all seeded user accounts can be queried through the UserRepository domain entity."
        // AI Contribution: Initial draft (~85%)
        // Modifications: Correct userRepository method name
        // Verification: JPA UserRepository query method assertion
        // Confidence: High
        @ParameterizedTest
        @CsvSource({"admin", "manager", "cashier"})
        @DisplayName("Seeded accounts are queryable through UserRepository domain model")
        void shouldFindSeededUsersViaRepository(String username) {
            Optional<User> userOptional = userRepository.findByUsernameWithRolesAndPermissions(username);

            assertThat(userOptional).isPresent();
            assertThat(userOptional.get().getUsername()).isEqualTo(username);
        }
    }
}