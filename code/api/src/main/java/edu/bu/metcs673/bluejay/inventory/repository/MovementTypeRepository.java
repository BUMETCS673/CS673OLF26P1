// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Repository interface for fetching MovementType by code.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.repository;

import edu.bu.metcs673.bluejay.inventory.entity.MovementType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create Spring Data JPA repository for MovementType lookup
// . Add query method to search by movement code."
// AI Contribution: Initial draft (~100%)
// Modifications: Derived query method added for code lookup.
// Verification: DataJpaTest unit test.
// Confidence: High
@Repository
public interface MovementTypeRepository extends JpaRepository<MovementType,
    Long> {
    Optional<MovementType> findByCode(String code);
}