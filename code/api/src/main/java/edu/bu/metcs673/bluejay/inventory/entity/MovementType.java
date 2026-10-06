// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: JPA Entity mapping for movement_types database table.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Map movement_types table to JPA entity. Define table
// columns and primary key generation."
// AI Contribution: Initial draft (~100%)
// Modifications: Aligned column mappings with Flyway migration schema.
// Verification: Schema auto-validation / JPA integration test.
// Confidence: High
@Entity
@Table(name = "movement_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovementType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column
    private String description;
}