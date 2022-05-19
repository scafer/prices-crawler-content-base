package io.github.scafer.prices.crawler.content.domain.service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductsDto {
    private String locale;
    private String catalog;
    private List<ProductDto> products;
    private Map<String, Object> data;
}
