package io.github.scafer.prices.crawler.content.controller;

import io.github.scafer.prices.crawler.content.domain.service.base.ProductService;
import io.github.scafer.prices.crawler.content.domain.service.dto.*;
import io.github.scafer.prices.crawler.content.domain.service.provider.ProductServiceProvider;
import io.github.scafer.prices.crawler.content.repository.implementation.ProductDataServiceImp;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@ConditionalOnProperty("prices.crawler.product.controller.enabled")
public class ProductController {
    private final ProductDataServiceImp productDataService;
    private final ProductServiceProvider productServiceProvider;

    public ProductController(ProductDataServiceImp productDatabaseService, ProductServiceProvider productServiceProvider) {
        this.productDataService = productDatabaseService;
        this.productServiceProvider = productServiceProvider;
    }

    @CrossOrigin
    @PostMapping("/products/search")
    public Mono<Collection<SearchProductsDto>> searchProducts(@RequestBody SearchQueryDto searchQueryDto) {
        var responses = new ArrayList<Mono<SearchProductsDto>>();

        for (String catalog : searchQueryDto.getCatalogs()) {
            var productService = getProductServiceFromCatalog(catalog);
            responses.add(productService.searchItem(searchQueryDto.getQuery()));
        }

        return Mono.zipDelayError(responses, objects -> new ArrayList(Arrays.asList(objects)));
    }

    @CrossOrigin
    @GetMapping("/products/search/{locale}/{catalog}/{productUrl}")
    public Mono<SearchProductDto> searchProduct(@PathVariable String locale, @PathVariable String catalog, @PathVariable String productUrl) {
        var catalogReference = String.format("%s.%s", locale, catalog);
        return getProductServiceFromCatalog(catalogReference).searchItemByProductUrl(productUrl);
    }

    @CrossOrigin
    @PostMapping("/products/list/update")
    public Mono<Collection<UpdateProductsDto>> updateProducts(@RequestBody List<UpdateProductsDto> searchQuery) {

        var responses = new ArrayList<Mono<UpdateProductsDto>>();

        for (var search : searchQuery) {
            var productService = getProductServiceFromCatalog(String.format("%s.%s", search.getLocale(), search.getCatalog()));
            responses.add(productService.updateItem(search));
        }

        return Mono.zipDelayError(responses, objects -> new ArrayList(Arrays.asList(objects)));
    }

    @CrossOrigin
    @GetMapping("/products/history/{locale}/{catalog}/{reference}")
    public ProductDataDto productHistory(@PathVariable String locale, @PathVariable String catalog, @PathVariable String reference,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
                                         @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate endDate) {

        return productDataService.findProduct(locale, catalog, reference)
                .map(value ->
                        new ProductDataDto(value).withPricesHistory(startDate, endDate))
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s product reference not found", reference)));
    }

    private ProductService getProductServiceFromCatalog(String catalogAlias) {
        try {
            return productServiceProvider.getServiceFromCatalog(catalogAlias);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s catalog not found", catalogAlias));
        }
    }
}