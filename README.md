# pub-sub
## How to run code
A docker compose file has been provided in docker folder. Below are the steps to run code
1) Make sure `docker` is installed.
2) Navigate to `docker` directory.
3) Run `docker-compose-up`. This will spin up a postgres db instance and the pub-sub service.

## How to test
1) You can go to `webhook.site` to get temporory webhook endpoint
2) To create topic, use 
```
POST /{topic} - create a new topic
```
```
curl --location --request POST 'localhost:8080/test-topic'
```

3) To subscribe to topic, use 
``` 
POST /{topic}/subscribe - subscribe to a topic
{
“subscriber”: “xyz_123”,
“webhook_endpoing”: “http://example”
}
```
```
curl --location 'localhost:8080/test-topic/subscribe' \
--header 'Content-Type: application/json' \
--data '{
    "subscriber":"xyz_124",
    "webhook_endpoint": "https://webhook.site/3504a9ea-fbdf-4b4f-b3be-bad112a9f03ea"
}'
```

4) To unsubscribe to topic, use
```
POST /{topic}/unsubscribe - unsubscribe to a topic
{
“subscriber”: “xyz_123”,
}
 ```
```
curl --location 'localhost:8080/test-topic/unsubscribe' \
--header 'Content-Type: application/json' \
--data '{
    "subscriber" : "xyz_124"
}'
```
5) To publish message use
```
POST /{topic}/publish - publish a message to a topic
{
 “message”: “hello, webb.ai!”
}
 ```
```
curl --location 'localhost:8080/test-topic/publish' \
--header 'Content-Type: application/json' \
--data '{
    "message": "How are you?"
}'
```

### HLD
![HLD Doc](https://github.com/ppopli/pub-sub/edit/main/hld.png)

### DB Schem
![DB Schema] (https://github.com/ppopli/pub-sub/edit/main/db-schema.png)



