package io.github.scafer.prices.crawler.content.domain.repository.dao;

import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;
import io.github.scafer.prices.crawler.content.domain.util.DateTimeUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document("product-incidents")
public class ProductIncidentDao {
    @Id
    private String id;
    private String oldProductId;
    private List<ProductDto> newProducts;
    private String description;
    private boolean merged = false;
    private boolean closed = false;
    private int hits = 1;
    private String created;
    private String updated;
    private Map<String, Object> data;

    public ProductIncidentDao(String oldProductId, ProductDto newProduct) {
        this.id = oldProductId;
        this.oldProductId = oldProductId;
        this.newProducts = List.of(newProduct);
        this.created = DateTimeUtils.getCurrentDateTimeString();
        this.updated = DateTimeUtils.getCurrentDateTimeString();
    }

    public ProductIncidentDao closed() {
        this.closed = true;
        return this;
    }

    public ProductIncidentDao merged() {
        this.merged = true;
        return this;
    }

    public void incrementHits() {
        this.hits++;
    }

    public void addNewProduct(ProductDto newProduct) {
        this.newProducts.add(newProduct);
    }
}
