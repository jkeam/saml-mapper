#!/bin/bash

mvn clean package
docker cp ./ear-module/target/saml-mapper-ear.ear keycloak:/opt/jboss/keycloak/standalone/deployments
