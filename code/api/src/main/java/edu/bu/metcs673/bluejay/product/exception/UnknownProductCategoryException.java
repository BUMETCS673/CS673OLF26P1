package edu.bu.metcs673.bluejay.product.exception;

public class UnknownProductCategoryException extends RuntimeException {
    public UnknownProductCategoryException(int categoryId) {
        super(String.format("Category with id %d does not exist.", categoryId));
    }
}
