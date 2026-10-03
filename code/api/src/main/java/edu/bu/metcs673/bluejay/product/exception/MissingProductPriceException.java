package edu.bu.metcs673.bluejay.product.exception;

public class MissingProductPriceException extends RuntimeException {
    public MissingProductPriceException(String productName) {
        super(String.format("Price of product with name '%s' has not been set", productName));
    }
}
