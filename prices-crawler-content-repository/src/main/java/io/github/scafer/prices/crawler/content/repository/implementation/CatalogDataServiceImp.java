package io.github.scafer.prices.crawler.content.repository.implementation;

import io.github.scafer.prices.crawler.content.domain.repository.CatalogDataRepository;
import io.github.scafer.prices.crawler.content.domain.repository.dao.CatalogDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.LocaleDao;
import io.github.scafer.prices.crawler.content.domain.service.data.CatalogDataService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatalogDataServiceImp implements CatalogDataService {
    private final CatalogDataRepository catalogDataRepository;

    public CatalogDataServiceImp(CatalogDataRepository catalogDataRepository) {
        this.catalogDataRepository = catalogDataRepository;
    }

    @Override
    public Optional<LocaleDao> findLocale(String locale) {
        return catalogDataRepository.findById(locale);
    }

    @Override
    public List<LocaleDao> findAllLocales() {
        return catalogDataRepository.findAll();
    }

    @Override
    public Optional<CatalogDao> findCatalog(String locale, String catalog) {
        var optionalLocaleDao = catalogDataRepository.findById(locale);

        if (optionalLocaleDao.isPresent()) {
            var localeDao = optionalLocaleDao.get();
            return localeDao.getCatalogs().stream().filter(it -> it.getReference().equals(catalog)).findFirst();
        }

        return Optional.empty();
    }

    @Override
    public void uploadData(List<LocaleDao> locales) {
        catalogDataRepository.saveAll(locales);
    }
}
