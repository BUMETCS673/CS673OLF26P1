// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Add Spring Data JPA implementation for product repository with barcode lookup and filtered pagination"
// AI Contribution: JpaRepository conversion, barcode lookup, and query-backed filtered pagination helpers (~85%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.repository;

import edu.bu.metcs673.bluejay.product.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import jakarta.persistence.LockModeType;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {
    Optional<Product> findByBarcode(String barcode);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Optional<Product> findByIdForUpdate(@Param("productId") UUID productId);

    @Query("""
        SELECT p
        FROM Product p
        LEFT JOIN p.category c
                WHERE (:searchTerm IS NULL
                             OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
                             OR p.barcode = :searchTerm)
                    AND (:productName IS NULL OR p.name LIKE CONCAT('%', :productName, '%'))
          AND (:categoryName IS NULL OR c.name = :categoryName)
                ORDER BY CASE WHEN p.barcode = :searchTerm THEN 0 ELSE 1 END, p.name
        """)
    Page<Product> findProducts(
        @Param("searchTerm") String searchTerm,
        @Param("productName") String productName,
        @Param("categoryName") String categoryName,
        Pageable pageable
    );

    default Product addProduct(Product product) {
        return save(product);
    }

    default Optional<Product> getProductBy(String barcode) {
        return findByBarcode(barcode);
    }

    default Page<Product> getProducts(
        String searchTerm,
        String productName,
        String categoryName,
        int pageNumber,
        int pageSize
    ) {
        return findProducts(
            searchTerm,
            productName,
            categoryName,
            PageRequest.of(pageNumber - 1, pageSize)
        );
    }

    default List<Product> getProducts(
        String productName,
        String categoryName,
        int pageNumber,
        int pageSize
    ) {
        return getProducts(null, productName, categoryName, pageNumber, pageSize);
    }
}
