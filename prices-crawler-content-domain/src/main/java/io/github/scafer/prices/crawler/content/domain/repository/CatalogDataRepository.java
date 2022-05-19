package io.github.scafer.prices.crawler.content.domain.repository;

import io.github.scafer.prices.crawler.content.domain.repository.dao.LocaleDao;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatalogDataRepository extends MongoRepository<LocaleDao, String> {

}
