# Message Broker
[RabbitMQ](https://www.rabbitmq.com/) is used as message broker.

## Run in docker
```
docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

* http://localhost:15672/ (guest:guest)
* http://localhost:15672/api/

## Install locally
[Generic install guide](https://www.rabbitmq.com/install-generic-unix.html)

## References
* [Concepts](https://www.rabbitmq.com/tutorials/amqp-concepts.html)
* [Java Client](https://www.rabbitmq.com/java-client.html) and [Java API guide](https://www.rabbitmq.com/api-guide.html)
