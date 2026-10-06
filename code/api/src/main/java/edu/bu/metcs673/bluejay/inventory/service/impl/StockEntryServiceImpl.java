// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Service orchestration for stock entry, barcode
// checking, quantity validation, stock incrementing, and audit movement
// logging.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification,
// refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.service.impl;

import edu.bu.metcs673.bluejay.common.exception.InvalidQuantityException;
import edu.bu.metcs673.bluejay.common.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.common.security.SecurityUtils;
import edu.bu.metcs673.bluejay.inventory.domain.MovementTypeCode;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryRequest;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryResponse;
import edu.bu.metcs673.bluejay.inventory.entity.InventoryMovement;
import edu.bu.metcs673.bluejay.inventory.entity.MovementType;
import edu.bu.metcs673.bluejay.inventory.repository.InventoryMovementRepository;
import edu.bu.metcs673.bluejay.inventory.repository.MovementTypeRepository;
import edu.bu.metcs673.bluejay.inventory.service.StockEntryService;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

// AI-ASSISTED: YES
// Tool: Gemini
// Prompt Summary: "Implement service logic for stock entry flow satisfying
// AC 1, AC 2, and AC 3. Atomically update current stock, update cost price,
// and record movement audit."
// AI Contribution: Initial draft (~100%)
// Modifications: Ensured transactional boundary and explicit exceptions
// matching acceptance criteria.
// Verification: Service unit tests, mockito testing.
// Confidence: High
@Service
@RequiredArgsConstructor // Automatically builds the constructor for you
public class StockEntryServiceImpl implements StockEntryService {

    private final ProductRepository productRepository;
    private final InventoryMovementRepository movementRepository;
    private final MovementTypeRepository movementTypeRepository;
    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public StockEntryResponse processStockEntry(StockEntryRequest request) {
        // AC 2: Defensive check for positive quantity
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new InvalidQuantityException();
        }

        // AC 1: Validate barcode existence in product catalog
        Product product = productRepository.findByBarcode(request.barcode()).orElseThrow(
            ProductNotFoundException::new);

        // Update product current stock count
        int updatedStock = product.getCurrentStock() + request.quantity();
        product.setCurrentStock(updatedStock);

        // Update product cost price if provided
        if (request.cost() != null &&
            request.cost().compareTo(BigDecimal.ZERO) >= 0) {
            product.setCostPrice(request.cost());
        }

        productRepository.save(product);

        // Fetch RESTOCK movement type entity
        MovementType restockType = movementTypeRepository.findByCode(
                MovementTypeCode.RESTOCK.name())
            .orElseThrow(() -> new IllegalStateException(
                "Movement type RESTOCK not found"));

        // AC 3: Log immutable audit record with authenticated user ID
        UUID userId = securityUtils.getCurrentUserId();
        InventoryMovement movement = new InventoryMovement();
        movement.setProductId(product.getId());
        movement.setQuantityChange(request.quantity());
        movement.setMovementType(restockType);
        movement.setUserId(userId);

        InventoryMovement savedMovement = movementRepository.save(movement);

        return new StockEntryResponse(
            savedMovement.getId(),
            String.valueOf(product.getId()),
            product.getBarcode(),
            product.getName(),
            request.quantity(),
            product.getCurrentStock(),
            product.getCostPrice(),
            String.valueOf(userId),
            LocalDateTime.now(),
            product.getName() + " stock entry recorded."
        );
    }
}