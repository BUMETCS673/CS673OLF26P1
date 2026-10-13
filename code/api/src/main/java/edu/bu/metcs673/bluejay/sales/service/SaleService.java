/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 80%
    AI-Assisted Areas: Sale service implementation
    Human Contributions: Prompted what services might be needed after the UI was added
    Notes: Interface for the sale service
    Authors: Italia Tran
*/

package edu.bu.metcs673.bluejay.sales.service;

import edu.bu.metcs673.bluejay.sales.dto.PrintReceiptRequest;
import edu.bu.metcs673.bluejay.sales.dto.PrintReceiptResponse;
import edu.bu.metcs673.bluejay.sales.dto.SaleRequest;
import edu.bu.metcs673.bluejay.sales.dto.SaleResponse;

public interface SaleService {
    SaleResponse checkout(SaleRequest request);
    PrintReceiptResponse printReceipt(PrintReceiptRequest request);
}