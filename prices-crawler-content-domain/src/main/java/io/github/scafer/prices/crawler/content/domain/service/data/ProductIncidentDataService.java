package io.github.scafer.prices.crawler.content.domain.service.data;

import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductIncidentDao;
import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface ProductIncidentDataService {
    Optional<ProductIncidentDao> findIncident(String reference);

    CompletableFuture<Void> saveIncident(ProductDao oldProduct, ProductDto newProduct);

    void closeIncident(String key, boolean merge);

    List<ProductIncidentDao> findAllIncidents();

    void uploadData(List<ProductIncidentDao> productIncidents);
}
