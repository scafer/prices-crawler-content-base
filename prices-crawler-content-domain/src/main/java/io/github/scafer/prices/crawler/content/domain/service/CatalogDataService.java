package io.github.scafer.prices.crawler.content.domain.service;

import io.github.scafer.prices.crawler.content.domain.repository.dao.CatalogDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.LocaleDao;

import java.util.List;
import java.util.Optional;

public interface CatalogDataService {
    Optional<LocaleDao> findLocale(String locale);

    List<LocaleDao> findAllLocales();

    Optional<CatalogDao> findCatalog(String locale, String catalog);

    void uploadData(List<LocaleDao> locales);
}
