# Data Scanner Service
This service is responsible for scanning file system and writing meta-data about scanned files into 
ElasticSearch database.
* Internal APIs: ``itx.dataserver.services.filescanner.FileScannerService``
* External APis: work in progress.

## Architecture
![architecture](docs/architecture.svg)

### Implemented features
* __Init DB__ - initialize ElasticSearch indexes. This will delete all data in indices file-info, meta-data-info, unmapped-data. 
* __scan filesystem directory__ - recursively scans filesystem directory, produces data is written into ElasticSearch. 
  In case only subdirectory is scanned or re-scanned, existing data in ElasticSearch is database is merged with actual file system state. 
* __record unmapped data__ - special index unmapped-data is reserved for recording failed mappings and file system scan errors. 
  unmapped-data documents may be used for later offline analysis of data transformation errors. 

### ElasticSearch data structure
Relations between indices are on application level. 
Indices have fields in documents which are unique and may be used for query documents related to main __file-info__ index. 

![indices-relations](docs/indices-relations.svg)

ElasticSearch indices:
* __file-info__ - main index containing file info data obtained from filesystem. 
  This is primary data index, other indices contain complementary information to file-info. 
* __meta-data-info__ - index with meta-data-info for files, mostly bitmap images like JPEG photos.
* __unmapped-data__ - unmapped objects serialized in JSON or other file scanning errors.
  In case file scanning or other data mapping fails for particular file, data about this 
  incident is recorded for later analysis as document in unmapped-data index.  

Examples of [ElasticSearch queries](docs/elasticsearch-queries.md) covering various use-cases.

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