# Integration Testing for ElasticSearchService(s)
In order to run this integration test suite, __ElasticSearch 7.5.1__ instance must be running on 
[http://localhost:9200](http://localhost:9200). 

### Build and Run
Make sure ElasticSearch is running before executing the test suite.
```
gradle clean build -Dtest.profile=integration --stacktrace --info
```

### Install ElasticSearch
* Download ElasticSearch [binary package](https://www.elastic.co/downloads/past-releases/elasticsearch-7-5-1).
* Extract the package in any directory 
  ```
  tar xzvf elasticsearch-7.5.1-linux-x86_64.tar.gz
  ```

### Start ElasticSearch server
* go to ElasticSearch directory
  ```
  cd elasticsearch-7.5.1/bin
  ```
* start ElasticSearch server
  ```
  ./elasticsearch
  ```  
* check if ElasticSearch server is running by calling it's REST API
  ```
  GET http://localhost:9200/
  ```  
  