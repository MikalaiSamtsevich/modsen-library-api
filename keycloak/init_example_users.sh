#!/bin/bash

echo "* Request for authorization"
RESULT=`curl --location --request POST 'http://keycloak:9082/realms/master/protocol/openid-connect/token' \
--header 'Accept: */*' \
--header 'Host: keycloak:9082' \
--header 'Connection: keep-alive' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'client_id=admin-cli' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'username=admin' \
--data-urlencode 'password=admin'`

echo "echo token "
echo  $RESULT

echo "\n"
echo "* Recovery of the token"
TOKEN=`echo $RESULT | sed 's/.*access_token":"//g' | sed 's/".*//g'`

echo "\n"
echo "* Display token"
echo $TOKEN

echo "\n"
echo " * user creation\n"
curl -v http://keycloak:9082/admin/realms/spring-boot/users -H "Content-Type: application/json" -H "Authorization: bearer $TOKEN"   --data '{"email":"admin@gmail.com", "enabled":"true", "username": "admin", "firstName":"admin","lastName":"admin", "credentials": [ {"temporary": "false", "type": "password", "value": "admin"}], "emailVerified":"true", "groups": ["MANAGER"]}'
curl -v http://keycloak:9082/admin/realms/spring-boot/users -H "Content-Type: application/json" -H "Authorization: bearer $TOKEN"   --data '{"email":"user@gmail.com", "enabled":"true", "username": "user",  "firstName":"user","lastName":"user","credentials": [ {"temporary": "false", "type": "password", "value": "user"}], "emailVerified":"true", "groups": ["CUSTOMER"]}'
