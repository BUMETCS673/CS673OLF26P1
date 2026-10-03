// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Update mock product repository to support paginated retrieval"
// AI Contribution: Pagination mock method update (~25%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.mock;

import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockProductRepository implements ProductRepository {
    private final ArrayList<Product> products = new ArrayList<>();

    public MockProductRepository() {
        Product p1 = new Product();
        p1.setId(UUID.randomUUID());
        p1.setBarcode("barcode-mock-12345");
        p1.setName("product-mock");
        p1.setCategoryId(1);

        Product p2 = new Product();
        p2.setId(UUID.randomUUID());
        p2.setBarcode("barcode-existed-12345");
        p2.setName("product-existed");
        p2.setCategoryId(1);

        products.add(p1);
        products.add(p2);
    }

    public void reset() {
        products.clear();
    }

    @Override
    public Product addProduct(Product product) {
        // Should create new entity in real database implementation
        // rather than update Product domain class
        product.setId(UUID.randomUUID());
        products.add(product);
        return product;
    }

    @Override
    public Optional<Product> getProductBy(String barcode) {
        return products.stream()
                .filter(p -> p.getBarcode().equals(barcode))
                .findFirst();
    }

    @Override
    public List<Product> getProducts(int pageNumber, int pageSize) {
        int fromIndex = (pageNumber - 1) * pageSize;
        if (fromIndex >= products.size()) {
            return List.of();
        }

        int toIndex = Math.min(fromIndex + pageSize, products.size());
        return products.subList(fromIndex, toIndex);
    }
}
