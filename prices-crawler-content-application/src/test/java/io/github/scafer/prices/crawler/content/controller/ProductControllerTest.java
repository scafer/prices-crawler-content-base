package io.github.scafer.prices.crawler.content.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoClient;
import io.github.scafer.prices.crawler.content.domain.service.ProductDataService;
import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductsDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchQueryDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.UpdateProductsDto;
import io.github.scafer.prices.crawler.content.domain.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.util.EmbeddedMongoDBServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
class ProductControllerTest {
    private static EmbeddedMongoDBServer embeddedMongoDBServer;

    private final String PRODUCTS_SEARCH = "/api/v1/products/search";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private ProductDataService productDataService;

    @BeforeAll
    static void setup() {
        embeddedMongoDBServer = EmbeddedMongoDBServer.create().start();
    }

    @AfterAll
    static void tearDown() {
        embeddedMongoDBServer.stop();
    }

    @Test
    void productByQueryTest_OK() {
        var search = new SearchQueryDto(new String[]{"local.demo"}, "query");
        var entity = restTemplate.postForEntity(PRODUCTS_SEARCH, search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void productByQueryTest_NOTFOUND() {
        var search = new SearchQueryDto(new String[]{"local.fake-catalog"}, "product_01");
        var entity = restTemplate.postForEntity(PRODUCTS_SEARCH, search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void productByUrlTest_OK() {
        var search = String.format("/api/v1/products/search/%s/%s/%s", "local", "demo", "url.local");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void productByUrlTest_NOTFOUND() {
        var search = String.format("/api/v1/products/search/%s/%s/%s", "local", "fake-catalog", "url.local");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    @Test
    void updateProductsTest_OK() {
        var updateProducts = new UpdateProductsDto();
        updateProducts.setLocale("local");
        updateProducts.setCatalog("demo");
        updateProducts.setQuantity(1);
        updateProducts.setProductData(createDemoProductDto());
        var entity = restTemplate.postForEntity("/api/v1/products/list/update", List.of(updateProducts), JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void getProductByLocaleAndCatalogAnReferenceTest_OK() {
        createDemoProductData();
        var search = String.format("/api/v1/products/history/%s/%s/%s", "local", "demo", "1");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void getProductByLocaleAndCatalogAnReferenceTest_NOTFOUND() {
        var search = String.format("/api/v1/products/history/%s/%s/%s", "local", "fake-catalog", "1");
        var entity = restTemplate.getForEntity(search, JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    private void createDemoProductData() {
        var productResult = createDemoProductDto();
        var searchResult = new SearchProductsDto("local", "demo", List.of(productResult), null);
        productDataService.saveSearchResult(searchResult);
        var mongodbCollection = mongoClient.getDatabase("prices-crawler-db").getCollection("products");
        assertEquals(1, mongodbCollection.countDocuments());
    }

    private ProductDto createDemoProductDto() {
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
        return productResult;
    }
}
