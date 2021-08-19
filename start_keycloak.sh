#!/bin/bash

# You can run these to make sure keycloak is not running
# docker stop keycloak
# docker rm keycloak

# Start container, please replace DB_ADDR with your IP address
docker run --name keycloak -e DB_ADDR=192.168.1.11 -e DB_PORT=3306 -e DB_USER=root -e DB_PASSWORD=root -e DB_VENDOR=mysql -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin -p 9990:9990 -p 8180:8080 -p 8543:8443 jboss/keycloak:14.0.0
