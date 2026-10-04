package edu.bu.metcs673.bluejay.product.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ProductDto {
    private UUID id;
    private String name;
    private String barcode;
    private Long categoryId;
    private String categoryName;
    private String categoryDescription;
    private double price;
}
