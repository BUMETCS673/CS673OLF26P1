// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Spring Data JPA Repository interface for
// InventoryMovement entity.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.repository;

import edu.bu.metcs673.bluejay.inventory.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create Spring Data JPA repository for inventory movements
// . Provide CRUD operations for audit persistence."
// AI Contribution: Initial draft (~100%)
// Modifications: Standard JpaRepository implementation.
// Verification: DataJpaTest integration test.
// Confidence: High
@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long> {
}