// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.repository;

import edu.bu.metcs673.bluejay.product.domain.PriceBook;

import java.util.List;
import java.util.UUID;

public interface PriceBookRepository {
    List<PriceBook> getProductPricesBy(UUID productId);
}
