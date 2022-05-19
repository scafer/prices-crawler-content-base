package io.github.scafer.prices.crawler.content.repository.implementation;

import io.github.scafer.prices.crawler.content.domain.repository.ProductDataRepository;
import io.github.scafer.prices.crawler.content.domain.repository.dao.PriceDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductDao;
import io.github.scafer.prices.crawler.content.domain.service.ProductDataService;
import io.github.scafer.prices.crawler.content.domain.service.ProductIncidentDataService;
import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;
import io.github.scafer.prices.crawler.content.domain.service.dto.SearchProductsDto;
import io.github.scafer.prices.crawler.content.domain.util.IdUtils;
import io.github.scafer.prices.crawler.content.repository.implementation.utils.ProductUtils;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Log4j2
@Service
public class ProductDataServiceImp implements ProductDataService {
    private final ProductDataRepository productDataRepository;
    private final ProductIncidentDataService productIncidentDataService;
    @Value("${prices.crawler.product-incident.enabled:true}")
    private boolean isProductIncidentEnabled;

    public ProductDataServiceImp(ProductDataRepository productDataRepository, ProductIncidentDataService productIncidentDataService) {
        this.productDataRepository = productDataRepository;
        this.productIncidentDataService = productIncidentDataService;
    }

    @Override
    public Optional<ProductDao> findProduct(String locale, String catalog, String reference) {
        return productDataRepository.findById(IdUtils.parse(locale, catalog, reference));
    }

    @Override
    public CompletableFuture<Void> saveSearchResult(SearchProductsDto searchProductsDto) {
        return saveSearchResult(searchProductsDto, Strings.EMPTY);
    }

    @Override
    public CompletableFuture<Void> saveSearchResult(SearchProductsDto searchProductsDto, String query) {
        for (var productDto : searchProductsDto.getProducts()) {
            var optionalProduct = productDataRepository.findById(IdUtils.parse(searchProductsDto.getLocale(), searchProductsDto.getCatalog(), productDto.getReference()));

            if (optionalProduct.isPresent()) {
                var productData = optionalProduct.get();

                if (isProductIncident(productData, productDto)) {
                    productDataRepository.save(updatedProductData(productData, productDto, query));
                } else {
                    CompletableFuture.supplyAsync(() -> productIncidentDataService.saveIncident(productData, productDto));
                }
            } else {
                createNewProductData(searchProductsDto.getLocale(), searchProductsDto.getCatalog(), productDto, query);
            }
        }

        return null;
    }

    @Override
    public List<ProductDao> findAllProducts() {
        return productDataRepository.findAll();
    }

    @Override
    public void uploadData(List<ProductDao> products) {
        productDataRepository.saveAll(products);
    }

    private void createNewProductData(String locale, String catalog, ProductDto productDto, String query) {
        var product = new ProductDao(locale, catalog, productDto);
        product.setPricesHistory(List.of(new PriceDao(productDto)));
        product.setSearchTerms(List.of(query));
        productDataRepository.save(product);
    }

    private ProductDao updatedProductData(ProductDao productDao, ProductDto productDto, String query) {
        productDao.updateFromProduct(productDto).incrementHits();
        productDao.setPricesHistory(ProductUtils.parsePricesHistory(productDao.getPricesHistory(), new PriceDao(productDto)));
        productDao.setSearchTerms(ProductUtils.parseSearchTerms(productDao.getSearchTerms(), query));
        productDao.setEanUpcList(ProductUtils.parseEanUpcList(productDao.getEanUpcList(), productDto.getEanUpcList()));
        return productDao;
    }

    private boolean isProductIncident(ProductDao oldProduct, ProductDto newProduct) {
        return isProductIncidentEnabled &&
                (oldProduct.getName() == null || oldProduct.getName().equalsIgnoreCase(newProduct.getName())) &&
                (oldProduct.getBrand() == null || oldProduct.getBrand().equalsIgnoreCase(newProduct.getBrand())) &&
                (oldProduct.getProductUrl() == null || oldProduct.getProductUrl().equalsIgnoreCase(newProduct.getProductUrl())) &&
                (oldProduct.getDescription() == null || oldProduct.getDescription().equalsIgnoreCase(newProduct.getDescription()));
    }
}
