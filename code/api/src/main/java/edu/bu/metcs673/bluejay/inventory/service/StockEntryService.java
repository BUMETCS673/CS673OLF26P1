// No AI Contribution
package edu.bu.metcs673.bluejay.inventory.service;

import edu.bu.metcs673.bluejay.inventory.dto.StockEntryRequest;
import edu.bu.metcs673.bluejay.inventory.dto.StockEntryResponse;

public interface StockEntryService {

    StockEntryResponse processStockEntry(StockEntryRequest request);
}
