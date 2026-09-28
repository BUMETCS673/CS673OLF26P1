package edu.bu.metcs673.bluejay.product.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateProductDto {
    private String name;
    private String barcode;
    private int categoryId;
}
