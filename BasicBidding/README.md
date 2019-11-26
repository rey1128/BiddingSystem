# BasicBidding
### This is the implementation of Bidding System using vert.x

## Running it
1. use docker, download the [docker-compose.yml](https://github.com/rey1128/BiddingSystem/blob/master/BasicBidding/docker/docker-compose.yml), and run
``` bash
docker-compose up
```
configuration via **environment** block in docker-compose.yml

2. run locally with gradle, clone the repository
sh git clone https://github.com/rey1128/BiddingSystem.git

enter project directory
```bash
cd BiddingSystem/BasicBidding
```
make sure your redis is running locally, and start the BasicBidding by
```bash
gradle run
```
configuration via conf.json file (~BiddingSystem/BasicBidding/src/main/resources/conf/conf.json)

## Configurations
Four variables can be configured:
* http.server.port
number indicates port for http server, default is 8080
* endpoints
comma separated values indicating endpoints of bidders
* redis.host
host of redis server, default is localhost
* redis.port
number indicates port of redis server, default is 6379

Configurations can be passed by conf.json file or environment variables, environment variables will override conf file

## About Redis 
1. redis is aimed for providing the possibility for managing the endpoints dynamically;
2. endpoints can be configured by environment variables or conf.json;
3. everytime the application restart, the endpoints will be reloaded from configurations into redis, during the runtime of the application, it will fetch the endpoints from redis;
if redis is not available, the application will use the 4. endpoints from the configurations;
5. in addition, the application provides an API showing the current endpoints:
GET /api/endpoint
