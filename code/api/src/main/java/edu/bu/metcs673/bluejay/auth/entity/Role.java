// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: ~70%
// AI-Assisted Areas: JPA Entity mappings, ManyToMany JoinTable definition,
// equals/hashCode
// Human Contributions: Field requirements, description attribute inclusion
// Notes: Ensured eager fetch for security permissions and verified set
// initialization.
// Authors: Sara Orion

package edu.bu.metcs673.bluejay.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Create Spring Data JPA Entity classes for Role,
// Permission, and User mapped to MySQL schema"
// AI Contribution: Initial draft (~80%)
// Modifications:
//   - Configured @ManyToMany join table for role_permissions
//   - Initialized Set collections with @Builder.Default to prevent
//   NullPointerExceptions
// Verification:
//   - Manual review against V1__initial_schema.sql DDL
// Confidence: High
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 255)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Role role = (Role) o;
        return id != null && Objects.equals(id, role.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
