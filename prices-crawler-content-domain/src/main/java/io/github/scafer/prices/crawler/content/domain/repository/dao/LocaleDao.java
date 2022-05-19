package io.github.scafer.prices.crawler.content.domain.repository.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@Document("locales")
public class LocaleDao {
    @Id
    private String id;
    private String name;
    private String imageUrl;
    private String reference;
    private String description;
    private boolean isActive = true;
    private boolean isCacheEnabled = true;
    private boolean isHistoryEnabled = true;
    private String created;
    private String updated;
    private List<CatalogDao> catalogs;
    private Map<String, Object> data;
}
