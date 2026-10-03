package edu.bu.metcs673.bluejay.product.repository;

import edu.bu.metcs673.bluejay.product.domain.PriceBook;

import java.util.List;
import java.util.UUID;

public interface PriceBookRepository {
    List<PriceBook> getProductPricesBy(UUID productId);
}
