package edu.bu.metcs673.bluejay.product.exception;

import java.util.UUID;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException() {
        super("Product not found.");
    }

    public ProductNotFoundException(UUID id) {
        super(String.format("Product with id '%s' not found.", id));
    }
}
