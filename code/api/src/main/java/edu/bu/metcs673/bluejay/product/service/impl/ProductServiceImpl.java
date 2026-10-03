// AI-ASSISTED: NO
// Tool: NO
// Prompt Summary: "N/A"
// AI Contribution: 0%
// Confidence: High

package edu.bu.metcs673.bluejay.product.service.impl;

import edu.bu.metcs673.bluejay.product.domain.Category;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;
import edu.bu.metcs673.bluejay.product.exception.ProductAlreadyExistedException;
import edu.bu.metcs673.bluejay.product.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.product.exception.UnknownProductCategoryException;
import edu.bu.metcs673.bluejay.product.repository.CategoryRepository;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.product.service.ProductService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository _productRepository;
    private final CategoryRepository _categoryRepository;
    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository) {
        _productRepository = productRepository;
        _categoryRepository = categoryRepository;
    }

    @Override
    public Product createProduct(CreateProductWithCategoryDto productDto) {
        Product product = new Product();
        product.setBarcode(productDto.getBarcode());
        product.setName(productDto.getName());
        product.setCategoryId(productDto.getCategoryId());

        var p = _productRepository.getProductBy(productDto.getBarcode());

        if (p.isPresent()) {
            throw new ProductAlreadyExistedException(productDto.getBarcode());
        }

        var c = _categoryRepository.getCategoryBy(productDto.getCategoryId());
        if (c.isPresent()) {
            return _productRepository.addProduct(product);
        }

        var categoryEntity = new Category();
        categoryEntity.setName(productDto.getCategoryName());
        categoryEntity.setDescription(productDto.getCategoryDescription());
        var category = _categoryRepository.addCategory(categoryEntity);
        product.setCategoryId(category.getId());
        product.setCategory(category);

        return _productRepository.addProduct(product);
    }

    @Override
    public List<ProductDto> getProducts() {
        List<Product> products = _productRepository.getProducts();

        if (products.isEmpty()) {
            throw new ProductNotFoundException();
        }

        List<ProductDto> result = new ArrayList<>();
        for (Product p : products) {
            Optional<Category> c = _categoryRepository.getCategoryBy(p.getCategoryId());
            if (c.isEmpty()) {
                throw new UnknownProductCategoryException(p.getCategoryId());
            }

            var dto = new ProductDto();
            dto.setId(p.getId());
            dto.setName(p.getName());
            dto.setBarcode(p.getBarcode());
            dto.setCategoryId(p.getCategoryId());
            dto.setCategoryName(c.get().getName());
            dto.setCategoryDescription(c.get().getDescription());

            result.add(dto);
        }

        return result;
    }
}
