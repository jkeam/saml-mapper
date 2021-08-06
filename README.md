# SAML Mapper

Maps saml assertion attributes, but also augments missing attributes.  Will also create a user storage spi to be able to save keycloak user data in an external datastore.

## Prerequisites

### Mysql Database
We will start the Mysql database and database client with

```shell
docker-compose up
```

Wait for the logs to settle and navigate to [adminer](http://localhost:8280/?server=db).

Enter in the following login information.

| System   | MySQL   |
-----------|----------
| Server   | db      |
| Username | root    |
| Password | root    |

Click Login.  Then "Create database" and create a database name `keycloak`.

Then create another database named `custom_keycloak` and in that database use the "SQL command" option to create the following database:

```sql
create table if not exists users(
    username varchar(64) not null primary key,
    password varchar(64) null,
    email varchar(128) null,
    firstName varchar(128) null,
    lastName varchar(128) null,
    birthDate DATE null,
    enriched varchar(10) null
);
```

### Keycloak
Edit the `start_keycloak.sh` shell script to replace with your own IP address and then run the script.

## Build

```shell
mvn clean package
```

## Deploy

Assuming `keycloak` is the name of your _running_ docker container.

```shell
docker cp ./ear-module/target/saml-mapper-ear.ear keycloak:/opt/jboss/keycloak/standalone/deployments
```
