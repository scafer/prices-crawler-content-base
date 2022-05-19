package io.github.scafer.prices.crawler.content.domain.repository.dao;

import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;
import io.github.scafer.prices.crawler.content.domain.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.domain.util.IdUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document("products")
public class ProductDao {
    @Id
    private String id;
    private String locale;
    private String catalog;
    private String reference;
    private String name;
    private String brand;
    private String quantity;
    private String description;
    private String imageUrl;
    private String productUrl;
    private List<String> eanUpcList;
    private Boolean isActive = true;
    private int hits;
    private String created;
    private String updated;
    private List<String> searchTerms;
    private List<PriceDao> pricesHistory;
    private Map<String, Object> data;

    public ProductDao(String locale, String catalog, ProductDto productDto) {
        this.locale = locale;
        this.catalog = catalog;
        this.created = DateTimeUtils.getCurrentDateTimeString();
        updateFromProduct(productDto).incrementHits();
    }

    public ProductDao updateFromProduct(ProductDto productDto) {
        this.id = IdUtils.parse(getLocale(), getCatalog(), productDto.getReference());
        this.reference = productDto.getReference();
        this.name = productDto.getName();
        this.brand = productDto.getBrand();
        this.quantity = productDto.getQuantity();
        this.description = productDto.getDescription();
        this.imageUrl = productDto.getImageUrl();
        this.productUrl = productDto.getProductUrl();
        this.eanUpcList = productDto.getEanUpcList();
        this.updated = DateTimeUtils.getCurrentDateTimeString();
        this.data = productDto.getData();
        return this;
    }

    public void incrementHits() {
        this.hits++;
    }

    public void incrementHits(int numberOfHits) {
        this.hits += numberOfHits;
    }
}
