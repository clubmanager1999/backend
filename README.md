# backend

## Build docker image locally (optional)

```shell
./gradlew bootBuildImage --imageName=ghcr.io/clubmanager1999/backend:latest
```

## Start

```shell
docker compose up
```

## Add member

```shell
curl http://localhost:8080/api/members -H 'Content-Type: application/json' -d '{"userName": "robert.paulson", "firstName": "Robert", "lastName": "Paulson", "email": "robert.paulson@paper-street-soap.co", "address": {"street": "Paper Street", "streetNumber": 537, "zip": "19806", "city": "Bradford"}}'
```

## Get members

```shell
curl http://localhost:8080/api/members
```

