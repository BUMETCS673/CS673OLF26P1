/*
    AI-USAGE SUMMARY
    Tools: GitHub Copilot
    Overall AI Contribution: 100%
    AI-Assisted Areas: Added role repository interface used for CRUD operations to the role table
    Human Contributions: None
    Notes: The role repository interface
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.auth.repository;

import edu.bu.metcs673.bluejay.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
