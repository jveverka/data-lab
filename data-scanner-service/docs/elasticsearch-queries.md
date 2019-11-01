# ElasticSearch queries

## Script utils
* count files in the directory
  ```
  find ${ROOT_DIR_NAME} -type f | wc -l
  ```

## Queries for use-cases
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
      "track_total_hits": 1000000,
      "aggs" : {
          "unmappedTypes" : {
              "terms": {
                 "field": "type"
              }
          },
          "unmappedReasons" : {
              "terms": {
                 "field": "reason"
              }
          }
      }
  }
  ```
* get aggregated data, how many different file extensions and types is in index __file-info__
  ```
  POST http://127.0.0.1:9200/file-info/_search?size=0
  {
      "track_total_hits": 1000000,
      "aggs" : {
          "fileExtensions" : {
              "terms": {
                  "field": "extension",
              }
          }
          "filetypes" : {
              "terms": {
                  "field": "type",
              }
          }
      }
  }
  ```
* get aggregated data, camera vendor types
  ```
  POST http://127.0.0.1:9200/meta-data-info/_search?size=0
  {
      "track_total_hits": 1000000,
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
  