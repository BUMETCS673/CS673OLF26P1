/*
    AI-USAGE SUMMARY
    Tools: Github Copilot
    Overall AI Contribution: 90%
    AI-Assisted Areas: API creation
    Human Contributions: Prompting to make sure only authorized users can access the API
    Notes: Sales Controller class that primarily handles sales related API
    Authors: Italia Tran, Sara Orion
*/

package edu.bu.metcs673.bluejay.sales.controller;

import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import edu.bu.metcs673.bluejay.sales.dto.PrintReceiptRequest;
import edu.bu.metcs673.bluejay.sales.dto.PrintReceiptResponse;
import edu.bu.metcs673.bluejay.sales.dto.SaleRequest;
import edu.bu.metcs673.bluejay.sales.dto.SaleResponse;
import edu.bu.metcs673.bluejay.sales.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sales")
@RequiredArgsConstructor
public class SaleController {
    private final SaleService saleService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ApiResponse<SaleResponse>> checkout(
        @Valid @RequestBody SaleRequest request
    ) {
        SaleResponse response = saleService.checkout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
            ApiResponse.success(response, "Sale completed successfully")
        );
    }
    
    @PostMapping("/printReceipt")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CASHIER')")
    public ResponseEntity<ApiResponse<PrintReceiptResponse>> printReceipt(
        @Valid @RequestBody PrintReceiptRequest request
    ) {
        PrintReceiptResponse response = saleService.printReceipt(request);
        return ResponseEntity.status(HttpStatus.OK).body(
            ApiResponse.success(response)
        );
    }
}