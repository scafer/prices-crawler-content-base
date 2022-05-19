package io.github.scafer.prices.crawler.content.service.cache;

import io.github.scafer.prices.crawler.content.domain.service.dto.ProductDto;
import io.github.scafer.prices.crawler.content.domain.util.DateTimeUtils;
import io.github.scafer.prices.crawler.content.domain.util.IdUtils;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;

@Log4j2
public class LocalProductCacheService {

    private static final Map<String, List<ProductDto>> cachedProducts = new HashMap<>();

    private LocalProductCacheService() {
        throw new IllegalStateException("Utility class");
    }

    public static void storeProductsList(String locale, String catalog, String reference, List<ProductDto> productDtos) {
        var key = IdUtils.parse(locale, catalog, reference);
        cachedProducts.computeIfAbsent(key, k -> productDtos);
    }

    public static boolean isProductListCached(String locale, String catalog, String reference) {
        var key = IdUtils.parse(locale, catalog, reference);
        return cachedProducts.containsKey(key);
    }

    public static List<ProductDto> retrieveProductsList(String locale, String catalog, String reference) {
        List<ProductDto> result = new ArrayList<>();
        var key = IdUtils.parse(locale, catalog, reference);

        if (cachedProducts.containsKey(key)) {
            var products = cachedProducts.get(key);
            var dateFormat = DateTimeUtils.getSimpleDateTimeFormat();

            try {
                var sampleProduct = products.stream().findFirst();

                if (sampleProduct.isPresent()) {
                    if (isSameDay(dateFormat.parse(DateTimeUtils.getCurrentDateTimeString()), dateFormat.parse(sampleProduct.get().getDate()))) {
                        log.info("Products Cache: returning {}", key);
                        result = products;
                    } else {
                        cachedProducts.remove(key);
                        log.info("Products Cache: removing {}", key);
                    }
                } else {
                    log.info("Products Cache: returning {} - empty", key);
                }
            } catch (Exception ex) {
                log.info("Products Cache: error - {}", ex.getMessage());
            }
        }

        return result;
    }
}
