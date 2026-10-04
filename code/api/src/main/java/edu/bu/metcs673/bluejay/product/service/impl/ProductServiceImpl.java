// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Refactor product service to use Product pricing fields, barcode lookup, and category selection"
// AI Contribution: Spring service integration for pricing, barcode lookup, and category list responses (~80%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.service.impl;

import edu.bu.metcs673.bluejay.product.domain.Category;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CategoryDto;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;
import edu.bu.metcs673.bluejay.product.exception.MissingProductPriceException;
import edu.bu.metcs673.bluejay.product.exception.ProductAlreadyExistedException;
import edu.bu.metcs673.bluejay.product.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.product.exception.UnknownProductCategoryException;
import edu.bu.metcs673.bluejay.product.repository.CategoryRepository;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.product.service.ProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
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
        product.setCostPrice(BigDecimal.ZERO);
        product.setSalePrice(BigDecimal.ZERO);

        var p = _productRepository.getProductBy(productDto.getBarcode());

        if (p.isPresent()) {
            throw new ProductAlreadyExistedException(productDto.getBarcode());
        }

        Optional<Category> c = productDto.getCategoryId() == null
            ? Optional.empty()
            : _categoryRepository.getCategoryBy(productDto.getCategoryId());
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
    public List<CategoryDto> getProductCategories() {
        List<Category> categories = _categoryRepository.getCategories();
        List<CategoryDto> result = new ArrayList<>();

        for (Category category : categories) {
            CategoryDto dto = new CategoryDto();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setDescription(category.getDescription());
            result.add(dto);
        }

        return result;
    }

    @Override
    public List<ProductDto> getProducts(int pageNumber, int pageSize) {
        if (pageNumber < 1) {
            throw new IllegalArgumentException("pageNumber must be greater than 0");
        }

        if (pageSize < 1) {
            throw new IllegalArgumentException("pageSize must be greater than 0");
        }

        List<Product> products = _productRepository.getProducts(
                pageNumber, pageSize);

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

    @Override
    public ProductDto getProductBy(String barcode) {
        Optional<Product> p = _productRepository.getProductBy(barcode);

        if (p.isEmpty()) {
            throw new ProductNotFoundException(barcode);
        }

        var product = p.get();
        Optional<Category> category = _categoryRepository.getCategoryBy(product.getCategoryId());
        if (category.isEmpty()) {
            throw new UnknownProductCategoryException(product.getCategoryId());
        }

        if (product.getSalePrice() == null
            || product.getSalePrice().compareTo(BigDecimal.ZERO) == 0) {
            throw new MissingProductPriceException(product.getName());
        }

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setBarcode(product.getBarcode());
        productDto.setCategoryId(product.getCategoryId());
        productDto.setCategoryName(category.get().getName());
        productDto.setCategoryDescription(category.get().getDescription());
        productDto.setPrice(product.getSalePrice().doubleValue());

        return productDto;
    }
}
