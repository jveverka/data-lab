# Integration Testing for ElasticSearchService(s)
In order to run integration tests, ElasticSearch instance must be running on http://localhost:9200. 

### Build and Run
```
gradle clean build -Dtest.profile=integration --stacktrace --info
```
