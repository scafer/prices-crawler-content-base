package io.github.scafer.prices.crawler.content.domain.service.dto;

import io.github.scafer.prices.crawler.content.domain.repository.dao.ProductDao;
import io.github.scafer.prices.crawler.content.domain.util.DateTimeUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ProductDataDto {
    private String locale;
    private String catalog;
    private String reference;
    private String name;
    private String quantity;
    private String brand;
    private String description;
    private String productUrl;
    private String imageUrl;
    private List<String> eanUpc;
    private List<String> searchTerms;
    private List<PriceDto> pricesHistory;
    private Map<String, Object> data;

    public ProductDataDto(ProductDao productDao) {
        this.locale = productDao.getLocale();
        this.catalog = productDao.getCatalog();
        this.reference = productDao.getReference();
        this.name = productDao.getName();
        this.quantity = productDao.getQuantity();
        this.brand = productDao.getBrand();
        this.description = productDao.getDescription();
        this.productUrl = productDao.getProductUrl();
        this.imageUrl = productDao.getImageUrl();
        this.eanUpc = productDao.getEanUpcList();
        this.pricesHistory = productDao.getPricesHistory().stream().map(PriceDto::new).collect(Collectors.toList());
        this.data = productDao.getData();
    }

    public ProductDataDto withPricesHistory(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return this;
        }

        try {
            var filteredPrices = new ArrayList<PriceDto>();

            for (var price : this.getPricesHistory()) {
                var localDateTime = LocalDateTime.parse(price.getDate(), DateTimeUtils.getDateTimeFormatter());
                var chronoLocalDate = ChronoLocalDate.from(localDateTime);

                if ((startDate.isBefore(chronoLocalDate) || startDate.isEqual(chronoLocalDate)) &&
                        (endDate.isAfter(chronoLocalDate) || endDate.isEqual(chronoLocalDate))) {
                    filteredPrices.add(price);
                }
            }
            this.pricesHistory = filteredPrices;
            return this;
        } catch (Exception ignore) {
            return this;
        }
    }
}
