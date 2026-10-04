// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: JPA Entity mapping for inventory_movements table with
// audit fields.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Map inventory_movements database table to JPA entity
// including user_id and timestamp. Handle foreign keys and timestamp
// auto-generation."
// AI Contribution: Initial draft (~100%)
// Modifications: Integrated user_id and reference_id schema updates from V3
// migration.
// Verification: Schema auto-validation / integration test.
// Confidence: High
@Entity
@Table(name = "inventory_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", columnDefinition = "VARCHAR(36)", updatable
        = false, nullable = false)
    private UUID productId;

    @Column(name = "quantity_change", nullable = false)
    private Integer quantityChange;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "movement_type_id", nullable = false)
    private MovementType movementType;

    @Column(name = "reference_id", length = 36)
    private String referenceId;

    @Column(name = "user_id", columnDefinition = "VARCHAR(36)")
    private UUID userId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}