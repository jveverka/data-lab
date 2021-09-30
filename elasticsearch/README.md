# ElasticSearch Service
This library was tested with __[ElasticSearch](https://www.elastic.co/downloads/elasticsearch) 7.15.0__.

* [ElasticSearch Service library](elasticsearch-service) - library implementation.
* [Integration Tests](elasticsearch-tests) - test suite for library functionality verification.

### Docker-Compose EK Stack
* Start EK stack in docker-compose
  ```
  docker-compose -f ek-docker-compose.yml up -d
  docker-compose -f ek-docker-compose.yml down -v
  ```
* Verify if ElasticSearch is running
  ```
  curl http://localhost:9200/_cat/indices
  ```
* Connect to Kibana UI ``http://localhost:5601``
 