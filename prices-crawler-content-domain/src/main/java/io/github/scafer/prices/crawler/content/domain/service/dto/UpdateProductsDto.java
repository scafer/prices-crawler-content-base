package io.github.scafer.prices.crawler.content.domain.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductsDto {
    private String key;
    private String locale;
    private String catalog;
    private ProductDto productData;
    private int quantity;
    private Map<String, Object> data;
}
