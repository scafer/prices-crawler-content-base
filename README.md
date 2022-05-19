# Prices Crawler Content Base

## 1. Description

The main goal of this project is to simplify the search of products prices in commerce websites and
store them for future comparison and analysis.

**Open API URL:** http://localhost:8080/swagger-ui.html

## 2. Environment Variables

| #   | Name           | Type   | Description          | Default |
|-----|----------------|--------|----------------------|---------|
| 1   | ACTIVE_PROFILE | String | Spring profile name  | -       |
| 2   | PORT           | int    | Service port         | 8080    |
| 3   | DATABASE_URL   | String | Database path url    | -       |
| 4   | DATABASE_NAME  | String | Database schema name | -       |

## 3. Spring Environment Properties

| #   | Name                                         | Type    | Description            | Default |
|-----|----------------------------------------------|---------|------------------------|---------|
| 1   | spring.data.mongodb.uri                      | String  | Mongodb URI            | -       |
| 2   | spring.data.mongodb.database                 | String  | Mongodb database name  | -       |
| 3   | prices.crawler.cache.enabled                 | Boolean | Cache service          | true    |
| 4   | prices.crawler.history.enabled               | Boolean | Prices history service | true    |
| 5   | prices.crawler.product-incident.enabled      | Boolean | Product incident check | true    |
| 6   | prices.crawler.catalog.controller.enabled    | Boolean | Catalog controller     | true    |
| 7   | prices.crawler.product.controller.enabled    | Boolean | Product controller     | true    |
| 8   | prices.crawler.management.controller.enabled | Boolean | Management controller  | true    |
