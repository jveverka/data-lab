# ElasticSearch Service
Library implementing easier access to ElasticSearch data.
Tested with __[ElasticSearch](https://www.elastic.co/downloads/elasticsearch) 7.5.1__

### Implemented features
* __Index operations__ - created, delete, check existence of ES indices.
* __Write data__ - save java POJO into as document into ES index. Java POJO = ES index.
* __Delete documents__ - remove document from ES index by it's ID.
* __Search indices__ - exec. search queries on the top of EX indices, results are automatically mapped to java POJOs.
* __Stream search__ - large queries are streamed for better performance.

### APIs
* [ElasticSearchService](src/main/java/itx/elastic/service/ElasticSearchService.java) - API to access ElasticSearch database.
* [ElasticSearchServiceImpl](src/main/java/itx/elastic/service/ElasticSearchServiceImpl.java) - implementation of ElasticSearchService.
* [DataTransformer](src/main/java/itx/elastic/service/DataTransformer.java) - API for java POJO operations.

### Build and Run
```
gradle clean build test publishToMavenLocal
```

### Use as dependency
* Maven dependency
  ```
  <dependency>
    <groupId>itx.elastic.service</groupId>
    <artifactId>elasticsearch-service</artifactId>
    <version>1.0.0</version>
  </dependency>
  ```
* Gradle dependency
  ```
  compile 'itx.elastic.service:elasticsearch-service:1.0.0'
  ```
  