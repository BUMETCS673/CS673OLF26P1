// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 80%
// AI-Assisted Areas: Implemented methods to get Inventory Health and Inventory List.
//     Implemented updateSalePrice service logic with UUID conversion, transactional boundary, and JPA persistence.
// Human Contributions: Ensured transactional boundary and explicit exceptions.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.service.impl;

import edu.bu.metcs673.bluejay.common.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryStockResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;
import edu.bu.metcs673.bluejay.inventory.service.InventoryService;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<InventoryStockResponse> getInventoryStock() {
        return productRepository.findAll().stream()
            .map(p -> new InventoryStockResponse(
                String.valueOf(p.getId()),
                p.getName(),
                p.getCurrentStock(),
                p.getLowStock()))
            .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventory() {
        return productRepository.findAll().stream()
            .map(p -> new InventoryResponse(
                    String.valueOf(p.getId()),
                    p.getBarcode(),
                    p.getName(),
                    p.getStockCost(),
                    p.getSalePrice(),
                    p.getCurrentStock()))
            .toList();

    }

    @Override
    @Transactional
    public InventoryResponse updateSalePrice(String productId, BigDecimal salePrice) {
        UUID id = UUID.fromString(productId);
        Product product = productRepository.findById(id)
            .orElseThrow(ProductNotFoundException::new);

        product.setSalePrice(salePrice);
        Product updatedProduct = productRepository.save(product);

        return new InventoryResponse(
            String.valueOf(updatedProduct.getId()),
            updatedProduct.getBarcode(),
            updatedProduct.getName(),
            updatedProduct.getStockCost(),
            updatedProduct.getSalePrice(),
            updatedProduct.getCurrentStock()
        );
    }
}