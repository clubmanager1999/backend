# backend

## Build docker image locally (optional)

```shell
./gradlew bootBuildImage --imageName=ghcr.io/clubmanager1999/backend:latest
```

## Start

```shell
docker compose up
```

## Issue admin token

```shell
export TOKEN=$(curl -X POST --user 'clubmanager1999-frontend:z4Zx8Z6nDWlNSU8mQpl0GWo9LTnFrMwO' http://localhost:8081/realms/clubmanager1999/protocol/openid-connect/token -H "Content-Type: application/x-www-form-urlencoded" -d 'grant_type=password&username=tyler.durden&password=chaos' | jq -r '.access_token')
```

## Add membership

```shell
curl http://localhost:8080/api/memberships -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"name": "Aspirant", "fee": 13.37}'
```

## Add member

```shell
curl http://localhost:8080/api/members -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' -d '{"userName": "robert.paulson", "firstName": "Robert", "lastName": "Paulson", "email": "robert.paulson@paper-street-soap.co", "address": {"street": "Paper Street", "streetNumber": 537, "zip": "19806", "city": "Bradford"}, "membership": {"id": 1}}'
```

## Get members

```shell
curl http://localhost:8080/api/members -H "Authorization: Bearer $TOKEN"
```

## Reset password

Open http://localhost:8025/

## Issue user token

```shell
export TOKEN=$(curl -X POST --user 'clubmanager1999-frontend:z4Zx8Z6nDWlNSU8mQpl0GWo9LTnFrMwO' http://localhost:8081/realms/clubmanager1999/protocol/openid-connect/token -H "Content-Type: application/x-www-form-urlencoded" -d 'grant_type=password&username=robert.paulson&password=secret' | jq -r '.access_token')
```

## Get profile

```shell
curl http://localhost:8080/api/profile -H "Authorization: Bearer $TOKEN"
```

