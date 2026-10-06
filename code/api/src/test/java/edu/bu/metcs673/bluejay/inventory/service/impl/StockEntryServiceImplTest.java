// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Unit tests for stock entry processing, validation, cost updates, and audit movement recording.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
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
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockEntryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private InventoryMovementRepository movementRepository;

    @Mock
    private MovementTypeRepository movementTypeRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private StockEntryServiceImpl stockEntryService;

    private Product existingProduct;
    private MovementType restockMovementType;
    private UUID mockUserId;
    private UUID mockProductId;

    @BeforeEach
    void setUp() {
        mockProductId = UUID.randomUUID();
        mockUserId = UUID.randomUUID();

        existingProduct = new Product();
        existingProduct.setId(mockProductId);
        existingProduct.setBarcode("123456");
        existingProduct.setName("Coffee Beans");
        existingProduct.setCurrentStock(10);
        existingProduct.setCostPrice(new BigDecimal("12.50"));

        restockMovementType = new MovementType();
        restockMovementType.setId(1L);
        restockMovementType.setCode(MovementTypeCode.RESTOCK.name());
    }

    @Test
    @DisplayName("processStockEntry should update stock, cost price, save product & movement when given valid request")
    void processStockEntry_Success_UpdatesStockCostAndSavesMovement() {
        StockEntryRequest request = new StockEntryRequest("123456", new BigDecimal("14.00"), 5);

        when(productRepository.findByBarcode("123456")).thenReturn(Optional.of(existingProduct));
        when(movementTypeRepository.findByCode(MovementTypeCode.RESTOCK.name())).thenReturn(Optional.of(restockMovementType));
        when(securityUtils.getCurrentUserId()).thenReturn(mockUserId);

        InventoryMovement savedMovementMock = new InventoryMovement();
        savedMovementMock.setId(100L);
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(savedMovementMock);

        StockEntryResponse response = stockEntryService.processStockEntry(request);

        assertNotNull(response);
        assertEquals(100L, response.movementId());
        assertEquals("Coffee Beans", response.productName());
        assertEquals(5, response.quantityAdded());
        assertEquals(15, response.newTotalStock());
        assertEquals(new BigDecimal("14.00"), response.costPrice());
        assertEquals(String.valueOf(mockUserId), response.userId());

        // Verify Product Stock & Cost updates saved
        verify(productRepository).save(existingProduct);
        assertEquals(15, existingProduct.getCurrentStock());
        assertEquals(new BigDecimal("14.00"), existingProduct.getCostPrice());

        // Verify InventoryMovement created with matching fields
        ArgumentCaptor<InventoryMovement> movementCaptor = ArgumentCaptor.forClass(InventoryMovement.class);
        verify(movementRepository).save(movementCaptor.capture());
        InventoryMovement capturedMovement = movementCaptor.getValue();

        assertEquals(mockProductId, capturedMovement.getProductId());
        assertEquals(5, capturedMovement.getQuantityChange());
        assertEquals(restockMovementType, capturedMovement.getMovementType());
        assertEquals(mockUserId, capturedMovement.getUserId());
    }

    @Test
    @DisplayName("processStockEntry should update stock without updating cost when cost is null or empty")
    void processStockEntry_Success_WithoutCostUpdate() {
        StockEntryRequest request = new StockEntryRequest("123456", null, 10);

        when(productRepository.findByBarcode("123456")).thenReturn(Optional.of(existingProduct));
        when(movementTypeRepository.findByCode(MovementTypeCode.RESTOCK.name())).thenReturn(Optional.of(restockMovementType));
        when(securityUtils.getCurrentUserId()).thenReturn(mockUserId);

        InventoryMovement savedMovement = new InventoryMovement();
        savedMovement.setId(101L);
        when(movementRepository.save(any(InventoryMovement.class))).thenReturn(savedMovement);

        stockEntryService.processStockEntry(request);

        assertEquals(20, existingProduct.getCurrentStock());
        assertEquals(new BigDecimal("12.50"), existingProduct.getCostPrice()); // Unchanged
    }

    @Test
    @DisplayName("processStockEntry should throw InvalidQuantityException when quantity is null or <= 0")
    void processStockEntry_InvalidQuantity_ThrowsException() {
        StockEntryRequest zeroQtyRequest = new StockEntryRequest("123456", new BigDecimal("10.00"), 0);
        StockEntryRequest nullQtyRequest = new StockEntryRequest("123456", new BigDecimal("10.00"), null);

        assertThrows(InvalidQuantityException.class, () -> stockEntryService.processStockEntry(zeroQtyRequest));
        assertThrows(InvalidQuantityException.class, () -> stockEntryService.processStockEntry(nullQtyRequest));

        verifyNoInteractions(productRepository, movementRepository);
    }

    @Test
    @DisplayName("processStockEntry should throw ProductNotFoundException when barcode does not exist")
    void processStockEntry_ProductNotFound_ThrowsException() {
        StockEntryRequest request = new StockEntryRequest("999999", new BigDecimal("10.00"), 5);
        when(productRepository.findByBarcode("999999")).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> stockEntryService.processStockEntry(request));

        verify(movementRepository, never()).save(any());
    }

    @Test
    @DisplayName("processStockEntry should throw IllegalStateException when RESTOCK movement type is missing")
    void processStockEntry_RestockTypeNotFound_ThrowsException() {
        StockEntryRequest request = new StockEntryRequest("123456", new BigDecimal("10.00"), 5);

        when(productRepository.findByBarcode("123456")).thenReturn(Optional.of(existingProduct));
        when(movementTypeRepository.findByCode(MovementTypeCode.RESTOCK.name())).thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> stockEntryService.processStockEntry(request));

        assertEquals("Movement type RESTOCK not found", exception.getMessage());
    }
}