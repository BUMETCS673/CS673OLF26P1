/*
No AI assistance.
 */

package edu.bu.metcs673.bluejay.inventory.service;

import edu.bu.metcs673.bluejay.inventory.dto.InventoryHealthResponse;
import edu.bu.metcs673.bluejay.inventory.dto.InventoryResponse;

import java.util.List;

public interface InventoryService {

    List<InventoryHealthResponse> getInventoryHealth();
    List<InventoryResponse> getInventory();

}
