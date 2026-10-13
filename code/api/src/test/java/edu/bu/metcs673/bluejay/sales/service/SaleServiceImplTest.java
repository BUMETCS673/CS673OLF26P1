package edu.bu.metcs673.bluejay.sales.service;

import edu.bu.metcs673.bluejay.common.exception.InsufficientCashReceivedException;
import edu.bu.metcs673.bluejay.common.exception.InsufficientProductStockException;
import edu.bu.metcs673.bluejay.common.security.SecurityUtils;
import edu.bu.metcs673.bluejay.inventory.entity.MovementType;
import edu.bu.metcs673.bluejay.inventory.repository.InventoryMovementRepository;
import edu.bu.metcs673.bluejay.inventory.repository.MovementTypeRepository;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.sales.domain.SaleTransaction;
import edu.bu.metcs673.bluejay.sales.dto.SaleLineRequest;
import edu.bu.metcs673.bluejay.sales.dto.SaleRequest;
import edu.bu.metcs673.bluejay.sales.dto.SaleResponse;
import edu.bu.metcs673.bluejay.sales.repository.SaleItemRepository;
import edu.bu.metcs673.bluejay.sales.repository.SaleTransactionRepository;
import edu.bu.metcs673.bluejay.sales.service.impl.SaleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceImplTest {
    private static final UUID PRODUCT_ID =
        UUID.fromString("12345678-1234-1234-1234-123456789abc");
    private static final UUID CASHIER_ID =
        UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Mock
    private ProductRepository productRepository;
    @Mock
    private SaleTransactionRepository saleTransactionRepository;
    @Mock
    private SaleItemRepository saleItemRepository;
    @Mock
    private InventoryMovementRepository inventoryMovementRepository;
    @Mock
    private MovementTypeRepository movementTypeRepository;
    @Mock
    private SecurityUtils securityUtils;

    private SaleServiceImpl saleService;

    @BeforeEach
    void setUp() {
        saleService = new SaleServiceImpl(
            productRepository,
            saleTransactionRepository,
            saleItemRepository,
            inventoryMovementRepository,
            movementTypeRepository,
            securityUtils
        );
    }

    @Test
    void completesSaleUsingDatabasePricesAndAggregatesDuplicateLines() {
        Product product = createProduct(10);
        when(productRepository.findByIdForUpdate(PRODUCT_ID))
            .thenReturn(Optional.of(product));
        when(securityUtils.getCurrentUserId()).thenReturn(CASHIER_ID);
        when(movementTypeRepository.findByCode("SALE"))
            .thenReturn(Optional.of(new MovementType(1L, "SALE", "Sale")));

        SaleResponse response = saleService.checkout(new SaleRequest(
            List.of(
                new SaleLineRequest(PRODUCT_ID, 1),
                new SaleLineRequest(PRODUCT_ID, 2)
            ),
            new BigDecimal("10.00")
        ));

        assertEquals(new BigDecimal("7.50"), response.grandTotal());
        assertEquals(new BigDecimal("2.50"), response.changeDue());
        assertEquals(1, response.items().size());
        assertEquals(3, response.items().getFirst().quantity());
        assertEquals(7, product.getCurrentStock());

        ArgumentCaptor<SaleTransaction> transactionCaptor =
            ArgumentCaptor.forClass(SaleTransaction.class);
        verify(saleTransactionRepository).save(transactionCaptor.capture());
        assertEquals(CASHIER_ID, transactionCaptor.getValue().getCashierId());
        assertEquals(new BigDecimal("7.50"), transactionCaptor.getValue().getGrandTotal());
        assertEquals("CASH", transactionCaptor.getValue().getPaymentMethod());

        verify(saleItemRepository).saveAll(anyList());
        verify(inventoryMovementRepository).saveAll(anyList());
        verify(productRepository).save(product);
    }

    @Test
    void rejectsInsufficientCashBeforeWritingSaleRecords() {
        when(productRepository.findByIdForUpdate(PRODUCT_ID))
            .thenReturn(Optional.of(createProduct(10)));

        assertThrows(InsufficientCashReceivedException.class, () ->
            saleService.checkout(new SaleRequest(
                List.of(new SaleLineRequest(PRODUCT_ID, 3)),
                new BigDecimal("7.49")
            ))
        );

        verify(saleTransactionRepository, never()).save(any());
        verify(saleItemRepository, never()).saveAll(anyList());
        verify(inventoryMovementRepository, never()).saveAll(anyList());
    }

    @Test
    void rejectsSaleWhenRequestedQuantityExceedsStock() {
        when(productRepository.findByIdForUpdate(PRODUCT_ID))
            .thenReturn(Optional.of(createProduct(2)));

        assertThrows(InsufficientProductStockException.class, () ->
            saleService.checkout(new SaleRequest(
                List.of(new SaleLineRequest(PRODUCT_ID, 3)),
                new BigDecimal("10.00")
            ))
        );

        verify(saleTransactionRepository, never()).save(any());
        verify(saleItemRepository, never()).saveAll(anyList());
        verify(inventoryMovementRepository, never()).saveAll(anyList());
    }

    private Product createProduct(int stock) {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("Spring Water");
        product.setBarcode("012345123455");
        product.setSalePrice(new BigDecimal("2.50"));
        product.setCostPrice(new BigDecimal("1.20"));
        product.setCurrentStock(stock);
        return product;
    }
}