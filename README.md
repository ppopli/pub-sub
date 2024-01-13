# pub-sub
## How to run code
A docker compose file has been provided in docker folder. Below are the steps to run code
1) Make sure `docker` is installed.
2) Navigate to `docker` directory.
3) Run `docker-compose-up`. This will spin up a postgres db instance and the pub-sub service.

## How to test
1) You can go to `webhook.site` to get temporory webhook endpoint
2) To create topic, use `POST /{topic} - create a new topic`
3) To subscribe to topic, use ``` POST /{topic}/subscribe - subscribe to a topic
{
“subscriber”: “xyz_123”,
“webhook_endpoing”: “http://example”
}

```
4) To unsubscribe to topic, use ```POST /{topic}/unsubscribe - unsubscribe to a topic
{
“subscriber”: “xyz_123”,
}
 ``` 
5) To publish message use ```POST /{topic}/publish - publish a message to a topic
{
 “message”: “hello, webb.ai!”
}
 ```
