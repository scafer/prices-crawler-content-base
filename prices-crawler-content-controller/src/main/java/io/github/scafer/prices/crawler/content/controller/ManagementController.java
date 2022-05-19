package io.github.scafer.prices.crawler.content.controller;

import io.github.scafer.prices.crawler.content.domain.repository.dao.LocaleDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductIncidentDao;
import io.github.scafer.prices.crawler.content.domain.service.CatalogDataService;
import io.github.scafer.prices.crawler.content.domain.service.ProductIncidentDataService;
import io.github.scafer.prices.crawler.content.repository.implementation.ProductDataServiceImp;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/management")
@ConditionalOnProperty("prices.crawler.management.controller.enabled")
public class ManagementController {
    private final CatalogDataService catalogDataService;
    private final ProductDataServiceImp productDataService;
    private final ProductIncidentDataService productIncidentDataService;

    public ManagementController(CatalogDataService catalogDataService, ProductDataServiceImp productDataService, ProductIncidentDataService productIncidentDataService) {
        this.catalogDataService = catalogDataService;
        this.productDataService = productDataService;
        this.productIncidentDataService = productIncidentDataService;
    }

    @GetMapping("/locales")
    public List<LocaleDao> backupLocalesData() {
        return catalogDataService.findAllLocales();
    }

    @PostMapping("/locales")
    public void uploadLocalesData(@RequestBody List<LocaleDao> locales) {
        catalogDataService.uploadData(locales);
    }

    @GetMapping("/products")
    public List<ProductDao> backupProductsData() {
        return productDataService.findAllProducts();
    }

    @PostMapping("/products")
    public void uploadProductsData(@RequestBody List<ProductDao> products) {
        productDataService.uploadData(products);
    }

    @GetMapping("/products/incidents/close/{incident}")
    public void closeProductIncident(@PathVariable String incident, @RequestParam boolean merge) {
        productIncidentDataService.closeIncident(incident, merge);
    }

    @GetMapping("/products/incidents")
    public List<ProductIncidentDao> backupProductIncidentsData() {
        return productIncidentDataService.findAllIncidents();
    }

    @PostMapping("/products/incidents")
    public void uploadProductIncidentsData(@RequestBody List<ProductIncidentDao> productIncidents) {
        productIncidentDataService.uploadData(productIncidents);
    }
}
