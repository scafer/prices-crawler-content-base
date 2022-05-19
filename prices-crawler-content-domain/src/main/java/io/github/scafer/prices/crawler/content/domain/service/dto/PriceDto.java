package io.github.scafer.prices.crawler.content.domain.service.dto;

import io.github.scafer.prices.crawler.content.domain.repository.dao.PriceDao;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class PriceDto {
    private String regularPrice;
    private String campaignPrice;
    private String pricePerQuantity;
    private String quantity;
    private String date;
    private Map<String, Object> data;

    public PriceDto(PriceDao priceDao) {
        this.regularPrice = priceDao.getRegularPrice();
        this.campaignPrice = priceDao.getCampaignPrice();
        this.pricePerQuantity = priceDao.getPricePerQuantity();
        this.quantity = priceDao.getQuantity();
        this.date = priceDao.getDate();
        this.data = priceDao.getData();
    }
}
