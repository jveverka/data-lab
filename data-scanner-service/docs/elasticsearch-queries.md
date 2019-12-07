# ElasticSearch queries
This is an example list of ElasticSearch queries covering some use cases.

## Script utils
* Count files in the directory recursively.
  ```
  find ${ROOT_DIR_NAME} -type f | wc -l
  ```

## Queries for use-cases
* Get data (data from all indices) for single file by fileInfoId
  ```
  GET http://127.0.0.1:9200/_search?q=fileInfoId:<fileInfoId>
  ```
* List all indices
  ```
  GET http://127.0.0.1:9200/_cat/indices?format=json&pretty=true
  ```
* Get all documents from index __file-info__
  ```
  GET http://localhost:9200/file-info/_search?pretty=true
  ```  
* Get number of jpeg files
  ```
  GET http://127.0.0.1:9200/file-info/_count?q=extension:jpg
  ```
* Filter by deviceInfo and vendor  
  ```
  GET http://localhost:9200/image-meta-data-info/_search?pretty=true&q=deviceInfo.vendor:Canon
  ```
* Count query count deviceInfo of specific vendor
  ```
  GET http://localhost:9200/image-meta-data-info/_count?q=deviceInfo.vendor:Canon
  ```
* Get index mapping
  ```
  GET http://127.0.0.1:9200/image-meta-data-info/_mapping
  ```
* Get aggregated data, count unmapped-data documents by type
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
* Get aggregated data, how many different file extensions and types is in index __file-info__
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
* Get aggregated data, camera vendor types
  ```
  POST http://127.0.0.1:9200/image-meta-data-info/_search?size=0
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
* Search by filepath using __regexp__. Assumption is that path is of keyword type.
  ```
  POST http://127.0.0.1:9200/file-info/_search
  {
       "track_total_hits": 1000000,
       "query": {
          "regexp": {
              "path": { 
                  "value": "/path/to/directory/.*"
              }
          }
      }
  }
  ```
* Search by radius distance from central point. (50km from center of Zilina, slovakia)
  ```
  GET http://127.0.0.1:9200/image-meta-data-info/_search
  {
      "query": {
          "bool" : {
              "must" : {
                  "match_all" : {}
              },
              "filter" : {
                  "geo_distance" : {
                      "distance" : "50km",
                      "gps.coordinates" : {
                          "lat" : 49.2224761,
                          "lon" : 18.7390965
                      }
                  }
              }
          }
      }
  }
  ```
* Get Object Categories recognized by ML services.
  ```
  GET http://127.0.0.1:9200/object-recognition/_search?size=0 
  {
  	  "track_total_hits": 100000,
      "aggs" : {
  			"vendorTypes" : {
  				"terms": {
  				  "field": "objects.classId",
  					"size" : 100
  				}
  			}
      }
  }	
  ```   