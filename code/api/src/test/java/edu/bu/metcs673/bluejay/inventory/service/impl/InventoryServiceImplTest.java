// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Unit testing for inventory health calculations and catalog mapping logic.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.service.impl;

import edu.bu.metcs673.bluejay.common.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryStockResponse;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product product;
    private UUID productId;

    @BeforeEach
    void setUp() {
        productId = UUID.randomUUID();
        product = new Product();
        product.setId(productId);
        product.setBarcode("1001");
        product.setName("Coffee Beans");
        product.setStockCost(new BigDecimal("10.00"));
        product.setSalePrice(new BigDecimal("15.00"));
        product.setCurrentStock(50);
        product.setLowStock(10);
    }

    @Test
    @DisplayName("getInventoryStock should map and return list of InventoryStockResponse")
    void getInventoryStock_ReturnsMappedStockList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<InventoryStockResponse> result = inventoryService.getInventoryStock();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(String.valueOf(productId), result.getFirst().id());
        assertEquals("Coffee Beans", result.getFirst().productName());
        assertEquals(50, result.getFirst().currentStock());
        assertEquals(10, result.getFirst().lowStockThreshold());
    }

    @Test
    @DisplayName("getInventory should map and return list of InventoryResponse")
    void getInventory_ReturnsMappedInventoryList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        List<InventoryResponse> result = inventoryService.getInventory();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(String.valueOf(productId), result.getFirst().id());
        assertEquals("1001", result.getFirst().barcode());
        assertEquals("Coffee Beans", result.getFirst().productName());
        assertEquals(new BigDecimal("10.00"), result.getFirst().stockCost());
        assertEquals(new BigDecimal("15.00"), result.getFirst().salePrice());
        assertEquals(50, result.getFirst().quantity());
    }

    @Test
    @DisplayName("updateSalePrice should update product sale price and return InventoryResponse")
    void updateSalePrice_Success_ReturnsUpdatedResponse() {
        BigDecimal newPrice = new BigDecimal("19.99");
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponse response = inventoryService.updateSalePrice(String.valueOf(productId), newPrice);

        assertNotNull(response);
        assertEquals(String.valueOf(productId), response.id());
        assertEquals(newPrice, response.salePrice());
        assertEquals(newPrice, product.getSalePrice());
        verify(productRepository).save(product);
    }

    @Test
    @DisplayName("updateSalePrice should throw ProductNotFoundException when product ID is not found")
    void updateSalePrice_ProductNotFound_ThrowsException() {
        UUID randomId = UUID.randomUUID();
        when(productRepository.findById(randomId)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
            () -> inventoryService.updateSalePrice(String.valueOf(randomId), new BigDecimal("19.99")));

        verify(productRepository, never()).save(any());
    }
}