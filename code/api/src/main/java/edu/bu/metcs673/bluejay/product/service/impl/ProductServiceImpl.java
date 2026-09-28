package edu.bu.metcs673.bluejay.product.service.impl;

import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CreateProductDto;
import edu.bu.metcs673.bluejay.product.exception.ProductAlreadyExistedException;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.product.service.ProductService;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository _productRepository;
    public ProductServiceImpl(ProductRepository productRepository) {
        _productRepository = productRepository;
    }

    @Override
    public Product createProduct(CreateProductDto productDto) {
        Product product = new Product();
        product.setBarcode(productDto.getBarcode());
        product.setName(productDto.getName());
        product.setCategoryId(productDto.getCategoryId());

        var p = _productRepository.getProductBy(productDto.getBarcode());

        if (p.isPresent()) {
            throw new ProductAlreadyExistedException(productDto.getBarcode());
        }

        return _productRepository.addProduct(product);
    }
}
