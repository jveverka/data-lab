[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/jveverka/data-lab.svg?branch=master)](https://travis-ci.org/jveverka/data-lab.svg?branch=master)

# Data Lab Project
__Data Lab Project__ provides advanced analytics and query services on various document sources like 
images, video streams, text documents, file system. This project is work in progress.
![datalab](docs/data-lab-image.svg)

## Features
* __File system indexing__ - queries on file system meta-data
* __Image meta-data indexing__ - queries on exif and geo-location meta data.
* __Video meta-data indexing__ - queries on exif and geo-location meta data.
* __Image content object recognition__ - queries on objects contained in images. 

### Microservices
* [__data-scanner-service__](data-scanner-service) - [__microservice__] simple service for scanning file system.
* [__ml-services__](ml-services) - [__microservices__] simple services utilizing using machine learning.

### Components 
* [__file-system-service__](file-system-service) - [__library__] simple library for scanning file system.
* [__elasticsearch-service__](elasticsearch) - [__library__] service for easy ElasticSearch read/write access.
* [__data-scanner-service__](data-scanner-service) - [__library__] service for scanning data directory and annotating data files.

### Architecture
![architecture](docs/architecture-01.svg)

### Technology stack
* __Microservices__ - REST, Message Broker integrations, K8s, WIP
* __ElasticSearch 7.x__ - main meta-data database
* __Kibana 7.x__ - basic data visualizations
* __Java 11__ - microservice implementations 
* __Python 3.6.x__ - microservice implementations
* __TensorFlow 2.0 / Keras__ - ML related tasks
* __Gradle 6.x__ - build system 

### Run and build
```
gradle clean build test
```
