// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Convert category model to a JPA entity for Spring Data repository support"
// AI Contribution: JPA entity mapping annotations and schema-aligned field configuration (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;
}
