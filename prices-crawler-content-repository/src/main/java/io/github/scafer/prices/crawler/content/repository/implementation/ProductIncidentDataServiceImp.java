package io.github.scafer.prices.crawler.content.repository.implementation;

import io.github.scafer.prices.crawler.content.domain.repository.ProductDataRepository;
import io.github.scafer.prices.crawler.content.domain.repository.ProductIncidentDataRepository;
import io.github.scafer.prices.crawler.content.domain.repository.dao.PriceDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductIncidentDao;
import io.github.scafer.prices.crawler.content.domain.service.data.ProductIncidentDataService;
import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;
import io.github.scafer.prices.crawler.content.domain.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.repository.implementation.util.ProductUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;

@Log4j2
@Service
public class ProductIncidentDataServiceImp implements ProductIncidentDataService {
    private final ProductIncidentDataRepository productIncidentDataRepository;
    private final ProductDataRepository productDataRepository;

    public ProductIncidentDataServiceImp(ProductIncidentDataRepository productIncidentDataRepository, ProductDataRepository productDataRepository) {
        this.productIncidentDataRepository = productIncidentDataRepository;
        this.productDataRepository = productDataRepository;
    }

    @Override
    public Optional<ProductIncidentDao> findIncident(String reference) {
        return productIncidentDataRepository.findById(reference);
    }

    @Override
    public CompletableFuture<Void> saveIncident(ProductDao oldProduct, ProductDto newProduct) {
        try {
            var optionalIncident = productIncidentDataRepository.findById(oldProduct.getId());

            if (optionalIncident.isPresent()) {
                var incident = optionalIncident.get();

                if (!incident.isClosed()) {
                    var lastStoredPrice = incident.getNewProducts().get(incident.getNewProducts().size() - 1);
                    var dateFormat = DateTimeUtils.getSimpleDateTimeFormat();

                    if (!isSameDay(dateFormat.parse(lastStoredPrice.getDate()), dateFormat.parse(newProduct.getDate()))) {
                        incident.addNewProduct(newProduct);
                    }

                    incident.incrementHits();
                    productIncidentDataRepository.save(incident);
                }
            } else {
                productIncidentDataRepository.save(new ProductIncidentDao(oldProduct.getId(), newProduct));
            }
        } catch (Exception exception) {
            log.error("Product Incident Exception. oldProduct - {}. newProduct - {}. Message - {}", oldProduct.getId(), newProduct.getReference(), exception.getMessage());
        }

        return null;
    }

    @Override
    public void closeIncident(String key, boolean merge) {
        var optionalProductIncident = productIncidentDataRepository.findById(key);

        if (merge && optionalProductIncident.isPresent()) {
            var productIncident = optionalProductIncident.get();
            var optionalProduct = productDataRepository.findById(productIncident.getId());

            if (optionalProduct.isPresent()) {
                var product = optionalProduct.get();
                product.incrementHits(productIncident.getHits());

                for (var newProduct : productIncident.getNewProducts()) {
                    product.updateFromProduct(newProduct);
                    product.setPricesHistory(ProductUtils.parsePricesHistory(product.getPricesHistory(), new PriceDao(newProduct)));
                    product.setSearchTerms(ProductUtils.parseSearchTerms(product.getSearchTerms(), null));
                    product.setEanUpcList(ProductUtils.parseEanUpcList(product.getEanUpcList(), newProduct.getEanUpcList()));
                }

                productDataRepository.save(product);
                productIncidentDataRepository.save(productIncident.merged().closed());
            }
        } else {
            optionalProductIncident.stream().findFirst().ifPresent(incident -> productIncidentDataRepository.save(incident.closed()));
        }
    }

    @Override
    public List<ProductIncidentDao> findAllIncidents() {
        return productIncidentDataRepository.findAll();
    }

    @Override
    public void uploadData(List<ProductIncidentDao> productIncidents) {
        productIncidentDataRepository.saveAll(productIncidents);
    }
}
