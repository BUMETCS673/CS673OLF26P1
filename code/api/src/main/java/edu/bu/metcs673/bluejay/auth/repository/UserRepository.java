// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: Spring Data JPA Repository interface, custom query
// method definition
// Human Contributions: Entity selection, query requirement specifications
// Notes: Eager fetching handled at entity level for roles/permissions during
// login lookup.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.repository;

import edu.bu.metcs673.bluejay.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create Spring Data JPA UserRepository for User entity"
// AI Contribution: Initial implementation (~80%)
// Modifications:
//   - Defined findByUsername query method for authentication lookups
// Verification:
//   - Code review against User entity field definitions
// Confidence: High
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT DISTINCT u FROM User u " +
        "LEFT JOIN FETCH u.roles r " +
        "LEFT JOIN FETCH r.permissions " +
        "WHERE u.username = :username")
    Optional<User> findByUsernameWithRolesAndPermissions(@Param("username") String username);

}