package io.github.scafer.prices.crawler.content.domain.service;

import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductDao;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductsDto;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ProductDataService {
    Optional<ProductDao> findProduct(String locale, String catalog, String reference);

    CompletableFuture<Void> saveSearchResult(SearchProductsDto searchProductsDto);

    CompletableFuture<Void> saveSearchResult(SearchProductsDto searchProductsDto, String query);

    List<ProductDao> findAllProducts();

    void uploadData(List<ProductDao> products);
}
