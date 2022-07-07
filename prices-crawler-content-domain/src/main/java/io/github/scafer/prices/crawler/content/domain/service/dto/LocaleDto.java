package io.github.scafer.prices.crawler.content.domain.service.dto;

import io.github.scafer.prices.crawler.content.domain.repository.dao.LocaleDao;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class LocaleDto {
    private String reference;
    private String name;
    private String imageUrl;
    private String description;
    private boolean isActive;
    private List<CatalogDto> catalogs;
    private Map<String, Object> data;

    public LocaleDto(LocaleDao localeDao) {
        this.name = localeDao.getName();
        this.imageUrl = localeDao.getImageUrl();
        this.reference = localeDao.getReference();
        this.description = localeDao.getDescription();
        this.isActive = localeDao.isActive();
        this.catalogs = localeDao.getCatalogs().stream().map(CatalogDto::new).toList();
        this.data = localeDao.getData();
    }
}
