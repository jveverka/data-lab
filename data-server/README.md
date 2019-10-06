# Data Server

### Run and Build
```
gradle clean installDist distZip test
```
```
build/install/data-server/bin/data-server -e <n> -p <path> -eh <elasstiHost> -ep <elasticPort>
# n - optional, thread pool size, default=1
# path - mandatory, /path/to/root/dir
# elasticHost - hostname of elasticsearch server, default="127.0.0.1"
# elasticPort - port of lasticsearch server, default=9200
```
