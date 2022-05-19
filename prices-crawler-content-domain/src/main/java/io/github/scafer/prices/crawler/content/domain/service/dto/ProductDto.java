package io.github.scafer.prices.crawler.content.domain.service.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ProductDto {
    private String reference;
    private String name;
    private String regularPrice;
    private String campaignPrice;
    private String pricePerQuantity;
    private String quantity;
    private String brand;
    private String description;
    private String productUrl;
    private String imageUrl;
    private List<String> eanUpcList;
    private String date;
    private Map<String, Object> data;
}
