package io.github.scafer.prices.crawler.content.controller;

import io.github.scafer.prices.crawler.content.domain.service.data.CatalogDataService;
import io.github.scafer.prices.crawler.content.domain.service.dto.LocaleDto;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@ConditionalOnProperty("prices.crawler.catalog.controller.enabled")
public class CatalogController {
    private final CatalogDataService catalogDataService;

    public CatalogController(CatalogDataService catalogDataService) {
        this.catalogDataService = catalogDataService;
    }

    @CrossOrigin
    @GetMapping("/locales")
    public List<LocaleDto> getLocales() {
        return catalogDataService.findAllLocales().stream()
                .map(LocaleDto::new).toList();
    }

    @CrossOrigin
    @GetMapping("/locales/{locale}")
    public LocaleDto getLocale(@PathVariable String locale) {
        return catalogDataService.findLocale(locale)
                .map(LocaleDto::new)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("%s locale not found", locale)));
    }
}
