package io.github.scafer.prices.crawler.content.domain.repository.dao;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Map;

@Data
@NoArgsConstructor
public class CatalogDao {
    @Id
    private String id;
    private String name;
    private String baseUrl;
    private String imageUrl;
    private String reference;
    private String description;
    private boolean isActive = true;
    private boolean isCacheEnabled = true;
    private boolean isHistoryEnabled = true;
    private String created;
    private String updated;
    private Map<String, Object> data;
}
