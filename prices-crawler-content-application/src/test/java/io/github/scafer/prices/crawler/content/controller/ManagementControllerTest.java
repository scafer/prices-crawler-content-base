package io.github.scafer.prices.crawler.content.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.mongodb.client.MongoClient;
import io.github.scafer.prices.crawler.content.domain.repository.CatalogDataRepository;
import io.github.scafer.prices.crawler.content.util.EmbeddedMongoDBServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {"ACTIVE_PROFILE=demo"})
class ManagementControllerTest {
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

    @ParameterizedTest
    @ValueSource(strings = {"locales", "products", "products/incidents"})
    void backupDataTest(String value) {
        var entity = restTemplate.getForEntity(String.format("/api/v1/management/%s", value), JsonNode.class);
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }
}
