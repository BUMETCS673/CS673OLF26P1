// AI-ASSISTED: YES
// Tool: GitHub Copilot
// Prompt Summary: "Refactor product retrieval service to use pagination"
// AI Contribution: Pagination refactor and guard clauses (~30%)
// Confidence: High

package edu.bu.metcs673.bluejay.product.service.impl;

import edu.bu.metcs673.bluejay.product.domain.Category;
import edu.bu.metcs673.bluejay.product.domain.PriceBook;
import edu.bu.metcs673.bluejay.product.domain.Product;
import edu.bu.metcs673.bluejay.product.dto.CreateProductWithCategoryDto;
import edu.bu.metcs673.bluejay.product.dto.ProductDto;
import edu.bu.metcs673.bluejay.product.exception.MissingProductPriceException;
import edu.bu.metcs673.bluejay.product.exception.ProductAlreadyExistedException;
import edu.bu.metcs673.bluejay.product.exception.ProductNotFoundException;
import edu.bu.metcs673.bluejay.product.exception.UnknownProductCategoryException;
import edu.bu.metcs673.bluejay.product.repository.CategoryRepository;
import edu.bu.metcs673.bluejay.product.repository.PriceBookRepository;
import edu.bu.metcs673.bluejay.product.repository.ProductRepository;
import edu.bu.metcs673.bluejay.product.service.ProductService;

import java.util.*;

public class ProductServiceImpl implements ProductService {
    private final ProductRepository _productRepository;
    private final CategoryRepository _categoryRepository;
    private final PriceBookRepository _priceBookRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              PriceBookRepository priceBookRepository) {
        _productRepository = productRepository;
        _categoryRepository = categoryRepository;
        _priceBookRepository = priceBookRepository;
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
    public ProductDto getProductBy(UUID id) {
        Optional<Product> p = _productRepository.getProductBy(id);

        if (p.isEmpty()) {
            throw new ProductNotFoundException(id);
        }

        var product = p.get();
        Optional<Category> category = _categoryRepository.getCategoryBy(product.getCategoryId());
        if (category.isEmpty()) {
            throw new UnknownProductCategoryException(product.getCategoryId());
        }

        List<PriceBook> priceBooks = _priceBookRepository.getProductPricesBy(id);
        if (priceBooks.isEmpty()) {
            throw new MissingProductPriceException(product.getName());
        }

        Optional<PriceBook> priceBook = priceBooks.stream()
                .max(Comparator.comparing(PriceBook::getEffectiveAt));

        ProductDto productDto = new ProductDto();
        productDto.setId(product.getId());
        productDto.setName(product.getName());
        productDto.setBarcode(product.getBarcode());
        productDto.setCategoryId(product.getCategoryId());
        productDto.setCategoryName(category.get().getName());
        productDto.setCategoryDescription(category.get().getDescription());
        productDto.setPrice(priceBook.get().getPrice());

        return productDto;
    }
}
