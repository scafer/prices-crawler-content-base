package io.github.scafer.prices.crawler.content.domain.service;

import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductsDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.UpdateProductsDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface ProductService {
    Mono<SearchProductsDto> searchItem(String query);

    Mono<SearchProductDto> searchItemByProductUrl(String productUrl);

    Mono<UpdateProductsDto> updateItem(UpdateProductsDto query);
}
