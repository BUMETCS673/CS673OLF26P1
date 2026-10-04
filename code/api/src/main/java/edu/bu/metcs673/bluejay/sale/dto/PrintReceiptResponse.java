package edu.bu.metcs673.bluejay.sale.dto;

public final record PrintReceiptResponse(
    String[] items,
    double totalPrice
) {
    
}