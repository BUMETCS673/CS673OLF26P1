// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Add Spring Data JPA implementation for product repository with barcode lookup and pagination helpers"
// AI Contribution: JpaRepository conversion, barcode lookup, and paging-backed pagination helpers (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.repository;

import edu.bu.metcs673.bluejay.product.domain.Product;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByBarcode(String barcode);

    default Product addProduct(Product product) {
        return save(product);
    }

    default Optional<Product> getProductBy(String barcode) {
        return findByBarcode(barcode);
    }

    default List<Product> getProducts(int pageNumber, int pageSize) {
        return findAll(PageRequest.of(pageNumber - 1, pageSize)).getContent();
    }
}
