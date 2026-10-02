// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.repository;

import edu.bu.metcs673.bluejay.product.domain.Category;

import java.util.Optional;

public interface CategoryRepository {
    Category addCategory(Category category);
    Optional<Category> getCategoryBy(int categoryId);
}
