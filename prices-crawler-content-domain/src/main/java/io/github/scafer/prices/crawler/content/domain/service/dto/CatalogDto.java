package io.github.scafer.prices.crawler.content.domain.service.dto;

import io.github.scafer.prices.crawler.content.domain.repository.dao.CatalogDao;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
public class CatalogDto {
    private String reference;
    private String name;
    private String baseUrl;
    private String imageUrl;
    private String description;
    private boolean isActive;
    private Map<String, Object> data;

    public CatalogDto(CatalogDao catalogDao) {
        this.reference = catalogDao.getReference();
        this.name = catalogDao.getName();
        this.baseUrl = catalogDao.getBaseUrl();
        this.imageUrl = catalogDao.getImageUrl();
        this.description = catalogDao.getDescription();
        this.isActive = catalogDao.isActive();
        this.data = catalogDao.getData();
    }
}
