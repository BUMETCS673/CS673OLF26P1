package edu.bu.metcs673.bluejay.sales.service.impl;

import edu.bu.metcs673.bluejay.common.exception.InsufficientCashReceivedException;
import edu.bu.metcs673.bluejay.common.exception.InsufficientProductStockException;
import edu.bu.metcs673.bluejay.common.exception.InvalidQuantityException;
import edu.bu.metcs673.bluejay.common.exception.MissingProductPriceException;
import edu.bu.metcs673.bluejay.common.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.common.security.SecurityUtils;
import edu.bu.metcs673.bluejay.inventory.domain.MovementTypeCode;
import edu.bu.metcs673.bluejay.inventory.entity.InventoryMovement;
import edu.bu.metcs673.bluejay.inventory.entity.MovementType;
import edu.bu.metcs673.bluejay.inventory.repository.InventoryMovementRepository;
import edu.bu.metcs673.bluejay.inventory.repository.MovementTypeRepository;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.sales.domain.SaleItem;
import edu.bu.metcs673.bluejay.sales.domain.SaleTransaction;
import edu.bu.metcs673.bluejay.sales.dto.ChangeDenominationResponse;
import edu.bu.metcs673.bluejay.sales.dto.PrintReceiptRequest;
import edu.bu.metcs673.bluejay.sales.dto.PrintReceiptResponse;
import edu.bu.metcs673.bluejay.sales.dto.SaleLineRequest;
import edu.bu.metcs673.bluejay.sales.dto.SaleLineResponse;
import edu.bu.metcs673.bluejay.sales.dto.SaleRequest;
import edu.bu.metcs673.bluejay.sales.dto.SaleResponse;
import edu.bu.metcs673.bluejay.sales.repository.SaleItemRepository;
import edu.bu.metcs673.bluejay.sales.repository.SaleTransactionRepository;
import edu.bu.metcs673.bluejay.sales.service.SaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {
    private static final List<Denomination> CHANGE_DENOMINATIONS = List.of(
        new Denomination(10000, "$100 bill"),
        new Denomination(5000, "$50 bill"),
        new Denomination(2000, "$20 bill"),
        new Denomination(1000, "$10 bill"),
        new Denomination(500, "$5 bill"),
        new Denomination(100, "$1 bill"),
        new Denomination(25, "quarter"),
        new Denomination(10, "dime"),
        new Denomination(5, "nickel"),
        new Denomination(1, "penny")
    );

    private final ProductRepository productRepository;
    private final SaleTransactionRepository saleTransactionRepository;
    private final SaleItemRepository saleItemRepository;
    private final InventoryMovementRepository inventoryMovementRepository;
    private final MovementTypeRepository movementTypeRepository;
    private final SecurityUtils securityUtils;
    
    @Override
    public PrintReceiptResponse printReceipt(PrintReceiptRequest request) {
        final var transactionOptional = saleTransactionRepository.findById(request.getTransactionId());
        if (!transactionOptional.isPresent()) {
            return null;
        }
        final var transaction = transactionOptional.get();
        final var saleItems = saleItemRepository.findByTransactionId(request.getTransactionId());
        final var saleItemsLine = new ArrayList<String>();
        saleItems.forEach(line -> {
            saleItemsLine.add(String.format("%s - %s - %s", "product", line.getQuantity(), line.getSubtotal()));
        });
        return new PrintReceiptResponse(
            request.getTransactionId(),
            transaction.getReceiptNumber(),
            transaction.getTransactionDate(),
            transaction.getGrandTotal(),
            transaction.getPaymentMethod(),
            saleItemsLine
        );
    }

    @Override
    @Transactional
    public SaleResponse checkout(SaleRequest request) {
        Map<UUID, Integer> requestedQuantities = aggregateItems(request.items());
        List<PreparedSaleLine> preparedLines = new ArrayList<>();
        BigDecimal grandTotal = BigDecimal.ZERO.setScale(2);

        for (Map.Entry<UUID, Integer> entry : requestedQuantities.entrySet()) {
            Product product = productRepository.findByIdForUpdate(entry.getKey())
                .orElseThrow(ProductNotFoundException::new);
            int quantity = entry.getValue();

            if (product.getCurrentStock() < quantity) {
                throw new InsufficientProductStockException(
                    product.getName(),
                    quantity,
                    product.getCurrentStock()
                );
            }
            if (product.getSalePrice() == null
                || product.getSalePrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new MissingProductPriceException(product.getName());
            }

            BigDecimal unitPrice = product.getSalePrice().setScale(2, RoundingMode.UNNECESSARY);
            BigDecimal unitCost = product.getCostPrice() == null
                ? BigDecimal.ZERO.setScale(2)
                : product.getCostPrice().setScale(2, RoundingMode.UNNECESSARY);
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
            grandTotal = grandTotal.add(subtotal);
            preparedLines.add(new PreparedSaleLine(
                product,
                quantity,
                unitCost,
                unitPrice,
                subtotal
            ));
        }

        BigDecimal cashReceived = request.cashReceived().setScale(2, RoundingMode.UNNECESSARY);
        if (cashReceived.compareTo(grandTotal) < 0) {
            throw new InsufficientCashReceivedException(cashReceived, grandTotal);
        }

        UUID cashierId = securityUtils.getCurrentUserId();
        UUID transactionId = UUID.randomUUID();
        String receiptNumber = "BJ-" + transactionId;
        BigDecimal changeDue = cashReceived.subtract(grandTotal);

        SaleTransaction transaction = new SaleTransaction();
        transaction.setId(transactionId);
        transaction.setReceiptNumber(receiptNumber);
        transaction.setCashierId(cashierId);
        transaction.setGrandTotal(grandTotal);
        transaction.setPaymentMethod("CASH");
        saleTransactionRepository.save(transaction);

        MovementType saleMovementType = movementTypeRepository
            .findByCode(MovementTypeCode.SALE.name())
            .orElseThrow(() -> new IllegalStateException("SALE movement type is missing."));

        List<SaleItem> saleItems = new ArrayList<>();
        List<InventoryMovement> inventoryMovements = new ArrayList<>();
        List<SaleLineResponse> responseLines = new ArrayList<>();

        for (PreparedSaleLine line : preparedLines) {
            Product product = line.product();
            product.setCurrentStock(product.getCurrentStock() - line.quantity());
            productRepository.save(product);

            SaleItem saleItem = new SaleItem();
            saleItem.setTransactionId(transactionId);
            saleItem.setProductId(product.getId());
            saleItem.setQuantity(line.quantity());
            saleItem.setUnitCost(line.unitCost());
            saleItem.setUnitPrice(line.unitPrice());
            saleItem.setSubtotal(line.subtotal());
            saleItems.add(saleItem);

            InventoryMovement movement = new InventoryMovement();
            movement.setProductId(product.getId());
            movement.setQuantityChange(-line.quantity());
            movement.setMovementType(saleMovementType);
            movement.setReferenceId(transactionId.toString());
            movement.setUserId(cashierId);
            inventoryMovements.add(movement);

            responseLines.add(new SaleLineResponse(
                product.getId(),
                product.getName(),
                line.quantity(),
                line.unitPrice(),
                line.subtotal()
            ));
        }

        saleItemRepository.saveAll(saleItems);
        inventoryMovementRepository.saveAll(inventoryMovements);

        return new SaleResponse(
            transactionId,
            receiptNumber,
            transaction.getTransactionDate(),
            grandTotal,
            cashReceived,
            changeDue,
            transaction.getPaymentMethod(),
            responseLines,
            calculateChangeBreakdown(changeDue)
        );
    }

    private Map<UUID, Integer> aggregateItems(List<SaleLineRequest> items) {
        Map<UUID, Integer> quantities = new TreeMap<>();
        for (SaleLineRequest item : items) {
            try {
                quantities.merge(item.productId(), item.quantity(), Math::addExact);
            } catch (ArithmeticException exception) {
                throw new InvalidQuantityException("Combined product quantity is too large.");
            }
        }
        return quantities;
    }

    private List<ChangeDenominationResponse> calculateChangeBreakdown(
        BigDecimal changeDue
    ) {
        long remainingCents = changeDue.movePointRight(2).longValueExact();
        List<ChangeDenominationResponse> breakdown = new ArrayList<>();
        for (Denomination denomination : CHANGE_DENOMINATIONS) {
            long count = remainingCents / denomination.valueCents();
            if (count > 0) {
                breakdown.add(new ChangeDenominationResponse(
                    denomination.valueCents(),
                    denomination.label(),
                    count
                ));
                remainingCents %= denomination.valueCents();
            }
        }
        return breakdown;
    }

    private record PreparedSaleLine(
        Product product,
        int quantity,
        BigDecimal unitCost,
        BigDecimal unitPrice,
        BigDecimal subtotal
    ) {
    }

    private record Denomination(long valueCents, String label) {
    }
}