package io.github.scafer.prices.crawler.content.domain.service.provider;

import io.github.scafer.prices.crawler.content.domain.service.ProductService;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceProvider extends GenericServiceProvider<ProductService> {
    public ProductServiceProvider(ApplicationContext appContext) {
        super(appContext, ProductService.class);
    }
}
