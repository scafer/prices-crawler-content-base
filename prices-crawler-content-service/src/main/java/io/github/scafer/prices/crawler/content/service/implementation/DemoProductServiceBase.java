package io.github.scafer.prices.crawler.content.service.implementation;

import io.github.scafer.prices.crawler.content.domain.service.CatalogDataService;
import io.github.scafer.prices.crawler.content.domain.service.ProductDataService;
import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductsDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.UpdateProductsDto;
import io.github.scafer.prices.crawler.content.domain.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.service.implementation.base.ProductServiceBase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Profile("demo")
@Qualifier("local.demo")
public class DemoProductServiceBase extends ProductServiceBase {
    public DemoProductServiceBase(CatalogDataService catalogDataService, ProductDataService productDatabaseService) {
        super("local", "demo", catalogDataService, productDatabaseService);
    }

    @Override
    protected CompletableFuture<SearchProductsDto> searchItemLogic(String query) {
        var productsResult = getDemoProducts();
        return CompletableFuture.completedFuture(new SearchProductsDto(locale, catalog, productsResult, generateDisplayOptions()));
    }

    @Override
    protected CompletableFuture<SearchProductDto> searchItemByProductUrlLogic(String productUrl) {
        var demoProducts = getDemoProducts();
        return CompletableFuture.completedFuture(new SearchProductDto(locale, catalog, demoProducts.get(0)));
    }

    @Override
    protected CompletableFuture<UpdateProductsDto> updateItemLogic(UpdateProductsDto query) {
        return CompletableFuture.completedFuture(null);
    }

    private List<ProductDto> getDemoProducts() {
        var productResult = new ProductDto();
        productResult.setReference("1");
        productResult.setName("Demo Product 1");
        productResult.setBrand("Demo Brand 1");
        productResult.setQuantity("1 /un");
        productResult.setDescription("Demo Description 1");
        productResult.setEanUpcList(List.of("123456789"));
        productResult.setDate(DateTimeUtils.getCurrentDateTimeString());
        productResult.setRegularPrice("1,20€");
        productResult.setCampaignPrice("1,00€");
        productResult.setPricePerQuantity("1,00€ /un");
        productResult.setProductUrl("http://demo-product-1.local");
        productResult.setImageUrl("http://demo-product-1.png");

        return List.of(productResult);
    }
}
