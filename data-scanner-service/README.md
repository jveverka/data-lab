# Data Scanner Service
This service is responsible for scanning file system and writing meta-data about scanned files into 
ElasticSearch database.

### Run and Build
```
gradle clean installDist distZip test
```
```
./build/install/data-scanner-service/bin/data-scanner-service -e <n> -p <path> -i <init> -eh <elasstiHost> -ep <elasticPort>
# n - optional, thread pool size, default=1
# path - mandatory, /path/to/root/dir
# elasticHost - optional, hostname of elasticsearch server, default="127.0.0.1"
# elasticPort - optional, port of lasticsearch server, default=9200
# init - optional, true|false init elasticsearch indices. all data in indec are lost if init = true. 

# Full directory scan (first time)
./build/install/data-scanner-service/bin/data-scanner-service -p /path/to/dir -i true

# Directory re-scan 
./build/install/data-scanner-service/bin/data-scanner-service -p /path/to/dir/subdir 
```

## ElasticSearch data structure

Indexes:
* __file-info__ - main index containing file info data
* __meta-data-info__ - index with meta-data for files
* __unmapped-data__ - unmapped objects serialized in JSON form for particular files 
* __*__ - all documents use fileInfoId to match query for single file

[ElasticSearch queries](docs/elasticsearch-queries.md)
