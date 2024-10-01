#!/bin/bash

echo "* Request for authorization"
RESULT=$(curl -X POST 'http://keycloak:9082/realms/master/protocol/openid-connect/token' \
--header 'Accept: */*' \
--header 'Host: keycloak:9082' \
--header 'Connection: keep-alive' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode "client_id=admin-cli" \
--data-urlencode "grant_type=password" \
--data-urlencode "username=admin" \
--data-urlencode "password=admin")

echo "echo token "
echo "$RESULT"

echo "* Recovery of the token"
TOKEN=$(echo "$RESULT" | sed 's/.*access_token":"//g' | sed 's/".*//g')

echo "* Display token"
echo "$TOKEN"

echo "* User creation"
curl -X POST http://keycloak:9082/admin/realms/spring-boot/users \
--header "Content-Type: application/json" \
--header "Authorization: Bearer $TOKEN" \
--data '{"email":"admin@gmail.com", "enabled":true, "username": "admin", "firstName":"admin","lastName":"admin", "credentials": [{"temporary": false, "type": "password", "value": "admin"}], "emailVerified":true, "groups": ["MANAGER"]}'

curl -X POST http://keycloak:9082/admin/realms/spring-boot/users \
--header "Content-Type: application/json" \
--header "Authorization: Bearer $TOKEN" \
--data '{"email":"user@gmail.com", "enabled":true, "username": "user", "firstName":"user","lastName":"user","credentials": [{"temporary": false, "type": "password", "value": "user"}], "emailVerified":true, "groups": ["CUSTOMER"]}'