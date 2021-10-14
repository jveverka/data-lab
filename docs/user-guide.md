# Data Lab User Guide

## Start Services locally
1. Start ElasticSearch and Kibana at localhost using [docker-compose](https://github.com/jveverka/guildelines-and-procedures/tree/master/docker/elastic-monitoring-stack)
2. Start object detection [ML service at localhost](../ml-services/od-yolov3-tf2)
3. Start data-scanner-service (top service for now)
   ```
   cd ../data-scanner-service
   ./run-scanner-example.sh /path/to/photo/directory
   ```  

## Query indexed data
After data scan is complete, use Kibana for data queries and 
visualization over ElasticSearch indices. 
