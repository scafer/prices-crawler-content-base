package io.github.scafer.prices.crawler.content.service.base;

import io.github.scafer.prices.crawler.content.domain.repository.dao.CatalogDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.LocaleDao;
import io.github.scafer.prices.crawler.content.domain.service.base.ProductService;
import io.github.scafer.prices.crawler.content.domain.service.cache.ProductCacheService;
import io.github.scafer.prices.crawler.content.domain.service.data.CatalogDataService;
import io.github.scafer.prices.crawler.content.domain.service.data.ProductDataService;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductsDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.UpdateProductsDto;
import org.springframework.beans.factory.annotation.Value;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ProductServiceBase implements ProductService {
    protected final String locale;
    protected final String catalog;
    private final CatalogDataService catalogDataService;
    private final ProductDataService productDatabaseService;
    private final ProductCacheService productCacheService;
    protected LocaleDao localeDao;
    protected CatalogDao catalogDao;
    @Value("${prices.crawler.cache.enabled:true}")
    private boolean isCacheEnabled;
    @Value("${prices.crawler.history.enabled:true}")
    private boolean isHistoryEnabled;

    protected ProductServiceBase(String locale, String catalog,
                                 CatalogDataService catalogDataService,
                                 ProductDataService productDatabaseService,
                                 ProductCacheService productCacheService) {
        this.locale = locale;
        this.catalog = catalog;
        this.catalogDataService = catalogDataService;
        this.productDatabaseService = productDatabaseService;
        this.productCacheService = productCacheService;

        catalogDataService.findLocale(locale)
                .ifPresent(value -> localeDao = value);

        catalogDataService.findCatalog(locale, catalog)
                .ifPresent(value -> catalogDao = value);
    }

    protected abstract CompletableFuture<SearchProductsDto> searchItemLogic(String query);

    protected abstract CompletableFuture<SearchProductDto> searchItemByProductUrlLogic(String productUrl);

    protected abstract CompletableFuture<UpdateProductsDto> updateItemLogic(UpdateProductsDto query);

    @Override
    public Mono<SearchProductsDto> searchItem(String query) {
        if (isLocaleOrCatalogDisabled()) {
            return Mono.just(new SearchProductsDto(locale, catalog, new ArrayList<>(), generateCatalogData()));
        }

        if (productCacheService.isProductListCached(locale, catalog, query)) {
            var cache = productCacheService.retrieveProductsList(locale, catalog, query);
            return Mono.just(new SearchProductsDto(locale, catalog, cache, generateCatalogData()));
        }

        var result = searchItemLogic(query)
                .thenApply(value -> saveProductsToDatabaseAndCache(value, query));

        return Mono.fromFuture(result);
    }

    @Override
    public Mono<SearchProductDto> searchItemByProductUrl(String productUrl) {
        if (isLocaleOrCatalogDisabled()) {
            return Mono.just(new SearchProductDto(locale, catalog, null));
        }

        var result = searchItemByProductUrlLogic(productUrl)
                .thenApply(this::saveProductToDatabase);

        return Mono.fromFuture(result);
    }

    @Override
    public Mono<UpdateProductsDto> updateItem(UpdateProductsDto query) {
        if (isLocaleOrCatalogDisabled()) {
            return Mono.just(query);
        }

        var result = updateItemLogic(query);
        return Mono.fromFuture(result);
    }

    protected Map<String, Object> generateCatalogData() {
        var displayOptions = new HashMap<String, Object>();

        catalogDataService.findCatalog(locale, catalog).stream().findFirst()
                .ifPresent(value -> displayOptions.put("catalogName", value.getName()));

        displayOptions.put("historyEnabled", isLocaleOrCatalogHistoryEnabled());

        return displayOptions;
    }

    private SearchProductDto saveProductToDatabase(SearchProductDto searchProductDto) {
        if (isHistoryEnabled && isLocaleOrCatalogHistoryEnabled()) {
            var searchResultDto = new SearchProductsDto(locale, catalog, List.of(searchProductDto.getProduct()), generateCatalogData());
            CompletableFuture.supplyAsync(() -> productDatabaseService.saveSearchResult(searchResultDto));
        }

        return searchProductDto;
    }

    private SearchProductsDto saveProductsToDatabaseAndCache(SearchProductsDto searchProductsDto, String query) {
        if (isHistoryEnabled && isLocaleOrCatalogHistoryEnabled()) {
            searchProductsDto.getProducts().stream()
                    .map(product -> new SearchProductDto(searchProductsDto.getLocale(), searchProductsDto.getCatalog(), product))
                    .forEach(this::saveProductToDatabase);
        }

        if (isCacheEnabled && isLocaleOrCatalogCacheEnabled()) {
            productCacheService.storeProductsList(locale, catalog, query, searchProductsDto.getProducts());
        }

        return searchProductsDto;
    }

    private boolean isLocaleOrCatalogHistoryEnabled() {
        return (localeDao == null && catalogDao == null) ||
                ((localeDao != null && localeDao.isHistoryEnabled()) && (catalogDao != null && catalogDao.isHistoryEnabled()));
    }

    private boolean isLocaleOrCatalogCacheEnabled() {
        return (localeDao == null && catalogDao == null) ||
                ((localeDao != null && localeDao.isCacheEnabled()) && (catalogDao != null && catalogDao.isCacheEnabled()));
    }

    private boolean isLocaleOrCatalogDisabled() {
        if (localeDao != null) {
            if (!localeDao.isActive()) {
                return !localeDao.isActive();
            }

            if (catalogDao != null) {
                return !catalogDao.isActive();
            }
        }

        return false;
    }
}
