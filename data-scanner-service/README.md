# Data Scanner Service

### Run and Build
```
gradle clean installDist distZip test
```
```
./build/install/data-scanner-service/bin/data-scanner-service -e <n> -p <path> -eh <elasstiHost> -ep <elasticPort>
# n - optional, thread pool size, default=1
# path - mandatory, /path/to/root/dir
# elasticHost - hostname of elasticsearch server, default="127.0.0.1"
# elasticPort - port of lasticsearch server, default=9200
```

## ElasticSearch indices
* __file-info__ - main index containing file info data
* __meta-data-info__ - index with meta-data for files
* __unmapped-data__ - unmapped objects serialized in JSON form for particular files 
* __*__ - all documents use fileInfoId to match query for single file

## ElasticSearch queries
* get data (data from all indices) for single file by fileInfoId
  ```
  GET http://127.0.0.1:9200/_search?q=fileInfoId:<fileInfoId>
  ```
* list all indices
  ```
  GET http://127.0.0.1:9200/_cat/indices?format=json&pretty=true
  ```
* get all documents from index __file-info__
  ```
  GET http://localhost:9200/file-info/_search?pretty=true
  ```  
* get number of jpeg files
  ```
  GET http://127.0.0.1:9200/file-info/_count?q=extension:jpg
  ```
* filter by deviceInfo and vendor  
  ```
  GET http://localhost:9200/meta-data-info/_search?pretty=true&q=deviceInfo.vendor:Canon
  ```
* count query count deviceInfo of specific vendor
  ```
  GET http://localhost:9200/meta-data-info/_count?q=deviceInfo.vendor:Canon
  ```
* get index mapping
  ```
  GET http://127.0.0.1:9200/meta-data-info/_mapping
  ```
* get aggregated data, count unmapped-data documents by type
  ```
  POST http://127.0.0.1:9200/unmapped-data/_search?size=0
  {
      "aggs" : {
          "vendorName" : {
              "terms": {
                 "field": "type",
                 "size": 10
              }
          }
      }
  }
  ```
* get aggregated data, how many different file extensions is in index
  ```
  POST http://127.0.0.1:9200/file-info/_search?size=0
  {
      "aggs" : {
  			"fileExtensions" : {
  				"terms": {
  				"field": "extension",
  				"size": 10
  				}
  			}
  
      }
  }
  ```
* get aggregated data, camera vendor types
  ```
  POST http://127.0.0.1:9200/meta-data-info/_search?size=0
  {
    "aggs" : {
			"vendorTypes" : {
				"terms": {
				  "field": "deviceInfo.vendor"
				}
			},
			"vendorModels" : {
				"terms": {
				  "field": "deviceInfo.model"
				}
			}
    }
  }	
  ```  
  