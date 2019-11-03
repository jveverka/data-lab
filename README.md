[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Build Status](https://travis-ci.org/jveverka/data-lab.svg?branch=master)](https://travis-ci.com/jveverka/data-lab.svg?token=GKfpyChfSfp1rNfoYwMM&branch=master)

# Data Lab
Big Data Lab

### Components
* [__file-system-service__](file-system-service) - [__microservice__] simple service for scanning file system
* [__elasticsearch-service__](elasticsearch) - [__library__] service for easy elasticsearch read/write access
* [__data-scanner-service__](data-scanner-service) - [__library__] service for scanning data directory and annotating data files

### Architecture
![architecture](docs/architecture-01.svg)

### Run and build
```
gradle clean build test
```

