# SAML Mapper

Maps saml assertion attributes, but also augments missing attributes.

## Build

Make sure that Keycloak SPI dependencies and your Keycloak server versions match. Keycloak SPI dependencies version is configured in `pom.xml` in the `keycloak.version` property.

To build the project execute the following command:

```bash
mvn package
```

## Deploy

And then, assuming `$KEYCLOAK_HOME` is pointing to you Keycloak installation, just copy it into deployments directory:

```bash
# cp target/keycloak-ip-authenticator.jar $KEYCLOAK_HOME/standalone/deployments/
docker cp ./target/saml-mapper.jar keycloak:/opt/jboss/keycloak/standalone
```
