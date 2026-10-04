package edu.bu.metcs673.bluejay.sale.controller;
import edu.bu.metcs673.bluejay.sale.dto.PrintReceiptRequest;
import edu.bu.metcs673.bluejay.common.dto.ApiResponse;
import edu.bu.metcs673.bluejay.sale.dto.PrintReceiptResponse;
import jakarta.validation.Valid;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Sale controller for cashier
*/
@RestController
@RequestMapping("/sale")
@NullMarked
public final class SaleController {
    @PostMapping("/printReceipt")
    public final ResponseEntity<ApiResponse<PrintReceiptResponse>> printReceipt(final @Valid @RequestBody PrintReceiptRequest printReceiptRequest) {
        
        // stub - need to write database repo connection
        
        ApiResponse<PrintReceiptResponse> response = ApiResponse.error(
            "Unable to find the requested transactionId",
            "NOT-FOUND"
        );
        return ResponseEntity.ok(response);
    }
}