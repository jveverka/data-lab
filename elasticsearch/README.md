# ElasticSearch Service
This library was tested with __[ElasticSearch](https://www.elastic.co/downloads/elasticsearch) 7.5.1__.

* [ElasticSearch Service library](elasticsearch-service) - library implementation.
* [Integration Tests](elasticsearch-tests) - test suite for library functionality verification.

### Install ElasticSearch
* Download ElasticSearch [binary package](https://www.elastic.co/downloads/elasticsearch).
* Extract the package in any directory 
  ```
  tar xzvf elasticsearch-7.5.1-linux-x86_64.tar.gz
  ```

### Configure ElasticSearch
To make ElasticSearch listen on all interfaces, ``config/elasticsearch.yml`` must contains this minimal configuration.
```
node.name: node-1
network.host: 0.0.0.0
cluster.initial_master_nodes: ["node-1"]
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

### Kibana Install, Setup and Start
* Download Kibana [binary package](https://www.elastic.co/downloads/kibana)
* Extract the package in any directory
  ```
  tar xzvf kibana-7.5.1-linux-x86_64.tar.gz
  ```
* configure kibana server to listen on all network interfaces,
  edit ``config/kibana.yaml`` so it contains this minimal configuration:
  ```
  server.host: "0.0.0.0"
  ```
* start kibana server
  ```
  cd kibana-7.5.1-linux-x86_64/bin
  ./kibana
  ```
* kibana server is available at local address ``http://192.168.44.33:5601/`` as well as on other interfaces.  
