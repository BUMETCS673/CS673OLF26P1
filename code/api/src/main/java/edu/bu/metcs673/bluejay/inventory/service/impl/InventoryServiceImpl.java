// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 80%
// AI-Assisted Areas: Implemented methods to get Inventory Health and Inventory List.
// Human Contributions: Ensured transactional boundary and explicit exceptions.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.service.impl;

import edu.bu.metcs673.bluejay.inventory.dto.InventoryHealthResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;
import edu.bu.metcs673.bluejay.inventory.service.InventoryService;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<InventoryHealthResponse> getInventoryHealth() {
        return productRepository.findAll().stream()
            .map(product -> {
                int percentage = Math.clamp(product.getCurrentStock(), 0, 100);
                String status = percentage > 50 ? "Healthy" : percentage > 20 ? "Low" : "Critical";
                return new InventoryHealthResponse(
                    String.valueOf(product.getId()),
                    product.getName(),
                    percentage,
                    status
                );
            })
            .toList();
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> getInventory() {
        return productRepository.findAll().stream()
            .map(p -> new InventoryResponse(
                    String.valueOf(p.getId()),
                    p.getBarcode(),
                    p.getName(),
                    p.getCostPrice(),
                    p.getCurrentStock()))
            .toList();

    }
}