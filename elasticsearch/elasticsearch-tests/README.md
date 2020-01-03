# Integration Testing for ElasticSearchService(s)
In order to run this integration test suite, __ElasticSearch 7.5.1__ instance must be running on 
[http://localhost:9200](http://localhost:9200). 

### Build and Run
Make sure ElasticSearch is running before executing the test suite.
```
gradle clean build -Dtest.profile=integration --stacktrace --info
```
