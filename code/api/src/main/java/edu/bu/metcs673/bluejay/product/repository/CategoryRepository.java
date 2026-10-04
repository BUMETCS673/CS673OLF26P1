// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Add Spring Data JPA implementation for category repository with category listing support"
// AI Contribution: JpaRepository extension and compatibility wrapper methods for create, lookup, and listing (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.repository;

import edu.bu.metcs673.bluejay.product.domain.Category;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    default Category addCategory(Category category) {
        return save(category);
    }

    default Optional<Category> getCategoryBy(Long categoryId) {
        return findById(categoryId);
    }

    default List<Category> getCategories() {
        return findAll(Sort.by(Sort.Direction.ASC, "name"));
    }
}
