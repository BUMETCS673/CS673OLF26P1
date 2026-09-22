// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: JPA Entity mappings, column definitions,
// equals/hashCode implementation
// Human Contributions: Field selection, database schema review, package
// structure
// Notes: Code verified against MySQL 8.4 schema constraints and Flyway
// migration V1.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.entity;

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

import java.util.Objects;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create Spring Data JPA Entity classes for Role,
// Permission, and User mapped to MySQL schema"
// AI Contribution: Initial draft (~80%)
// Modifications:
//   - Added explicit column length limits matching database schema
//   - Implemented JPA-compliant equals and hashCode based on primary key
// Verification:
//   - Manual review against V1__initial_schema.sql DDL
// Confidence: High
@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Permission that = (Permission) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}