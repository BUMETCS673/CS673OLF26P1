// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.mock;

import edu.bu.metcs673.bluejay.product.domain.Category;
import edu.bu.metcs673.bluejay.product.repository.CategoryRepository;

import java.util.ArrayList;
import java.util.Optional;

public class MockCategoryRepository implements CategoryRepository {
    private final ArrayList<Category> categories = new ArrayList<>();

    public MockCategoryRepository() {
        Category c1 = new Category();
        c1.setId(1);
        c1.setName("test-category");
        c1.setDescription("test-description");

        categories.add(c1);
    }

    @Override
    public Category addCategory(Category category) {
        category.setId(9999);
        categories.add(category);
        return category;
    }

    @Override
    public Optional<Category> getCategoryBy(int categoryId) {
        return categories.stream()
                .filter(c -> c.getId() == categoryId)
                .findFirst();
    }

    public void reset() {
        categories.clear();
    }
}
