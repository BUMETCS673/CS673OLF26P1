// AI-USAGE SUMMARY
// Tools: Gemini
// Overall AI Contribution: 100%
// AI-Assisted Areas: Unit testing for inventory health calculations and catalog mapping logic.
// Human Contributions: Custom business logic, validation, security refactoring.
// Notes: Initial code generated via AI; requires student verification, refactoring, and testing before integration.
// authors: Sara Orion

package edu.bu.metcs673.bluejay.inventory.service.impl;

import edu.bu.metcs673.bluejay.inventory.dto.InventoryHealthResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private Product healthyProduct;
    private Product lowProduct;
    private Product criticalProduct;

    @BeforeEach
    void setUp() {
        healthyProduct = new Product();
        healthyProduct.setId(UUID.randomUUID());
        healthyProduct.setBarcode("1001");
        healthyProduct.setName("Healthy Item");
        healthyProduct.setCostPrice(new BigDecimal("10.00"));
        healthyProduct.setCurrentStock(75);

        lowProduct = new Product();
        lowProduct.setId(UUID.randomUUID());
        lowProduct.setBarcode("1002");
        lowProduct.setName("Low Item");
        lowProduct.setCostPrice(new BigDecimal("15.00"));
        lowProduct.setCurrentStock(30);

        criticalProduct = new Product();
        criticalProduct.setId(UUID.randomUUID());
        criticalProduct.setBarcode("1003");
        criticalProduct.setName("Critical Item");
        criticalProduct.setCostPrice(new BigDecimal("5.00"));
        criticalProduct.setCurrentStock(10);
    }

    @Test
    @DisplayName("getInventoryHealth should accurately compute percentages and health status thresholds")
    void getInventoryHealth_ComputesCorrectStatusAndPercentages() {
        when(productRepository.findAll()).thenReturn(List.of(healthyProduct, lowProduct, criticalProduct));

        List<InventoryHealthResponse> healthResponses = inventoryService.getInventoryHealth();

        assertEquals(3, healthResponses.size());

        InventoryHealthResponse healthyRes = healthResponses.get(0);
        assertEquals("Healthy Item", healthyRes.productName());
        assertEquals(75, healthyRes.percentage());
        assertEquals("Healthy", healthyRes.status());

        InventoryHealthResponse lowRes = healthResponses.get(1);
        assertEquals("Low Item", lowRes.productName());
        assertEquals(30, lowRes.percentage());
        assertEquals("Low", lowRes.status());

        InventoryHealthResponse criticalRes = healthResponses.get(2);
        assertEquals("Critical Item", criticalRes.productName());
        assertEquals(10, criticalRes.percentage());
        assertEquals("Critical", criticalRes.status());

        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getInventoryHealth should return an empty list when no products exist")
    void getInventoryHealth_EmptyProductCatalog_ReturnsEmptyList() {
        when(productRepository.findAll()).thenReturn(Collections.emptyList());

        List<InventoryHealthResponse> result = inventoryService.getInventoryHealth();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getInventory should correctly map all product fields to InventoryResponse DTOs")
    void getInventory_MapsProductCatalogToResponses() {
        when(productRepository.findAll()).thenReturn(List.of(healthyProduct));

        List<InventoryResponse> inventoryResponses = inventoryService.getInventory();

        assertEquals(1, inventoryResponses.size());
        InventoryResponse response = inventoryResponses.get(0);

        assertEquals(String.valueOf(healthyProduct.getId()), response.id());
        assertEquals("1001", response.barcode());
        assertEquals("Healthy Item", response.productName());
        assertEquals(new BigDecimal("10.00"), response.cost());
        assertEquals(75, response.quantity());

        verify(productRepository, times(1)).findAll();
    }
}