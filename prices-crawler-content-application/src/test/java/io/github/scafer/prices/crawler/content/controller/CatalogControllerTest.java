package io.github.scafer.prices.crawler.content.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoClient;
import io.github.scafer.prices.crawler.content.domain.repository.CatalogDataRepository;
import io.github.scafer.prices.crawler.content.domain.repository.dao.CatalogDao;
import io.github.scafer.prices.crawler.content.domain.repository.dao.LocaleDao;
import io.github.scafer.prices.crawler.content.util.EmbeddedMongoDBServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
class CatalogControllerTest {
    private static EmbeddedMongoDBServer embeddedMongoDBServer;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private CatalogDataRepository catalogDataRepository;

    @BeforeAll
    static void setup() {
        embeddedMongoDBServer = EmbeddedMongoDBServer.create().start();
    }

    @AfterAll
    static void tearDown() {
        embeddedMongoDBServer.stop();
    }

    @Test
    void getLocales_OK() {
        var entity = restTemplate.getForEntity("/api/v1/locales", JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void getLocale_OK() {
        createDemoLocaleData();
        var entity = restTemplate.getForEntity("/api/v1/locales/local", JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    @Test
    void getLocale_NOTFOUND() {
        var entity = restTemplate.getForEntity("/api/v1/locales/fake-locale", JsonNode.class);
        assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
    }

    private void createDemoLocaleData() {
        var catalogDao = new CatalogDao();
        catalogDao.setId("local.demo");
        catalogDao.setReference("demo");
        catalogDao.setName("Demo");

        var localeDao = new LocaleDao();
        localeDao.setId("local");
        localeDao.setReference("local");
        localeDao.setName("Local");
        localeDao.setCatalogs(List.of(catalogDao));

        catalogDataRepository.save(localeDao);
        var mongodbCollection = mongoClient.getDatabase("prices-crawler-db").getCollection("locales");
        assertEquals(1, mongodbCollection.countDocuments());
    }
}
