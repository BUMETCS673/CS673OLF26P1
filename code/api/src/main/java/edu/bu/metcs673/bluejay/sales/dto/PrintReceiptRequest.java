package edu.bu.metcs673.bluejay.sales.dto;

import java.util.UUID;

public class PrintReceiptRequest {
    // Change this from String to UUID
    private UUID transactionId; 

    public UUID getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }
    
    public void setTransactionId(final String transactionId) {
        this.transactionId = UUID.fromString(transactionId);
    }
}