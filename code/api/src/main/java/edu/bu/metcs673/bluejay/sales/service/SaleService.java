package edu.bu.metcs673.bluejay.sales.service;

import edu.bu.metcs673.bluejay.sales.dto.PrintReceiptRequest;
import edu.bu.metcs673.bluejay.sales.dto.PrintReceiptResponse;
import edu.bu.metcs673.bluejay.sales.dto.SaleRequest;
import edu.bu.metcs673.bluejay.sales.dto.SaleResponse;

public interface SaleService {
    SaleResponse checkout(SaleRequest request);
    PrintReceiptResponse printReceipt(PrintReceiptRequest request);
}