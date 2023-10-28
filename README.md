# backend

## Build docker image locally (optional)

```shell
./gradlew bootBuildImage --imageName=ghcr.io/clubmanager1999/backend:latest
```

## Start

```shell
docker compose up
```

## Issue token

```shell
export TOKEN=$(curl -X POST --user 'clubmanager1999-frontend:z4Zx8Z6nDWlNSU8mQpl0GWo9LTnFrMwO' http://localhost:8081/realms/clubmanager1999/protocol/openid-connect/token -H "Content-Type: application/x-www-form-urlencoded" -d 'grant_type=password&username=tyler.durden&password=chaos' | jq -r '.access_token')
```

## Add member

```shell
curl http://localhost:8080/api/members -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"userName": "robert.paulson", "firstName": "Robert", "lastName": "Paulson", "email": "robert.paulson@paper-street-soap.co", "address": {"street": "Paper Street", "streetNumber": 537, "zip": "19806", "city": "Bradford"}}'
```

## Get members

```shell
curl http://localhost:8080/api/members -H "Authorization: Bearer $TOKEN"
```

