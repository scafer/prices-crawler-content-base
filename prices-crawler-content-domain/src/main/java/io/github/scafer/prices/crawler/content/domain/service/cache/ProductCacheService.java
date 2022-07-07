package io.github.scafer.prices.crawler.content.domain.service.cache;

import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;

import java.util.List;

public interface ProductCacheService {
    public void storeProductsList(String locale, String catalog, String reference, List<ProductDto> products);

    public boolean isProductListCached(String locale, String catalog, String reference);

    public List<ProductDto> retrieveProductsList(String locale, String catalog, String reference);
}
