package com.example.ludogoriesoft.lukeriaerpapi.dtos;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long id;
    @Min(value = 1, message = "Цената не може да бъде отрицателно число!")
    private BigDecimal price;
    private Long packageId;
    @Min(value = 1, message = "Наличните бройки не могат да бъдат отрицателно число!")
    private int availableQuantity;
    @Size(min = 2, max = 10, message = "Кода на продукта трябда да бъде между 2 и 10 символа!")
    private String productCode;
    private boolean forSale;
    private String barcode;
  public ProductDTO(BigDecimal price, Long packageId, int availableQuantity, String productCode) {
        this.price = price;
        this.packageId = packageId;
        this.availableQuantity = availableQuantity;
        this.productCode = productCode;
    }
}


