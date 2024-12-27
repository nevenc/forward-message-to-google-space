# forward-message-to-google-space

This is a simple Spring Boot app that forwards a webhook message received from Azure Container Registry to a Google Space.

## Configure 

Copy `src/main/resources/creds-template.yaml` to `src/main/resources/creds.yaml` and fill in the Google Space details.

## Deploy

Deploy to TPCF environment, e.g.

```
./mvnw clean package
cf push
```

## Test 

You can test locally, e.g.

```
http POST localhost:8080/webhook < message-example.json
```

