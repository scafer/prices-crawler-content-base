package io.github.scafer.prices.crawler.content.domain.repository.dao;

import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class PriceDao {
    private String regularPrice;
    private String campaignPrice;
    private String pricePerQuantity;
    private String quantity;
    private String date;
    private Map<String, Object> data;

    public PriceDao(ProductDto productDto) {
        this.regularPrice = productDto.getRegularPrice();
        this.campaignPrice = productDto.getCampaignPrice();
        this.pricePerQuantity = productDto.getPricePerQuantity();
        this.quantity = productDto.getQuantity() == null || productDto.getQuantity().isEmpty() ? productDto.getName() : productDto.getQuantity();
        this.date = productDto.getDate();
        this.data = productDto.getData();
    }
}
