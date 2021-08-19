# SAML Mapper

Maps saml assertion attributes, but also augments missing attributes.  Will also create a user storage spi to be able to save keycloak user data in an external datastore.

## Prerequisites

### Mysql Database
We will start the Mysql database and database client with

```shell
docker-compose up
```

Wait for the logs to settle and navigate to [adminer](http://localhost:8280/?server=db).

Enter the following login information.

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

1. Edit `start_keycloak.sh`.  There is a current limitation that the keycloak container requires the IP address of your machine so that it can connect to the database.  As such, edit `start_keycloak.sh` to replace with your own IP address.
2. Run the script, and fully wait until it is up.  It will take a few minutes.
```dtd
./start_keycloak.sh
```
3.  Open [management web console](http://localhost:8180/auth/admin/) and login with username `admin` and password `admin`
4.  Download this file named [quarkus-realm.json](https://raw.githubusercontent.com/jkeam/quarkus-keycloak/main/config/quarkus-realm.json)
5.  Add Realm and import the `quarkus-realm.json` file you just downloaded

## Build

```shell
mvn clean package
```

## Deploy

Wait for the build to complete.  Next run the following command.  Note, we are assuming `keycloak` is the name of your running docker container; which if you followed the instructions above it is.

```shell
docker cp ./ear-module/target/saml-mapper-ear.ear keycloak:/opt/jboss/keycloak/standalone/deployments
```
