package edu.bu.metcs673.bluejay.product.service.impl;

import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CreateProductDto;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.product.service.ProductService;

public class ProductServiceImpl implements ProductService {
    public ProductServiceImpl(ProductRepository productRepository) {}

    @Override
    public Product createProduct(CreateProductDto productDto) {
        return null;
    }
}
