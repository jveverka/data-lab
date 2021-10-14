#!/bin/bash

echo "PATH: $1"
./build/install/data-scanner-service/bin/data-scanner-service -e 1 -p $1 -eh 127.0.0.1 -ep 9200
