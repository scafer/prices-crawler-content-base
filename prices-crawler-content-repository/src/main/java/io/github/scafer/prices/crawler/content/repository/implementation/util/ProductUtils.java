package io.github.scafer.prices.crawler.content.repository.implementation.util;

import io.github.scafer.prices.crawler.content.domain.repository.dao.PriceDao;
import io.github.scafer.prices.crawler.content.domain.util.DateTimeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static org.apache.commons.lang3.time.DateUtils.isSameDay;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductUtils {

    public static List<String> parseEanUpcList(List<String> storedEanUpcList, List<String> eanUpcList) {
        if (storedEanUpcList == null) {
            return eanUpcList;
        }

        for (String eanUpc : eanUpcList) {
            if (!storedEanUpcList.contains(eanUpc)) {
                storedEanUpcList.add(eanUpc);
            }
        }

        return storedEanUpcList;
    }

    public static List<PriceDao> parsePricesHistory(List<PriceDao> storedPrices, PriceDao priceDao) {
        try {
            if (storedPrices == null) {
                return List.of(priceDao);
            }

            var lastStoredPrice = storedPrices.get(storedPrices.size() - 1);

            if (lastStoredPrice != null) {
                var dateFormat = DateTimeUtils.getSimpleDateTimeFormat();

                if (!isSameDay(dateFormat.parse(lastStoredPrice.getDate()), dateFormat.parse(priceDao.getDate()))) {
                    storedPrices.add(priceDao);
                }

            } else {
                storedPrices.add(priceDao);
            }
        } catch (Exception exception) {
            log.error("PricesHistory parser exception: {}", exception.getMessage());
        }

        return storedPrices;
    }

    public static List<String> parseSearchTerms(List<String> storedTerms, String term) {
        if (term == null) {
            return storedTerms;
        }

        if (storedTerms == null || term.isEmpty()) {
            return List.of(term);
        }

        if (storedTerms.stream().noneMatch(value -> value.equals(term.toLowerCase()))) {
            storedTerms.add(term.toLowerCase().trim());
        }

        return storedTerms;
    }
}
