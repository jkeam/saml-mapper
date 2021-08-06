package com.github.jkeam.keycloak.storageproviders;

import org.keycloak.component.ComponentModel;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.github.jkeam.keycloak.storageproviders.CustomUserStorageProviderConstants.*;

public class DbUtil {
    public static Connection getConnection(ComponentModel config) throws SQLException, NamingException {
        String driverClass = config.get(CONFIG_KEY_JDBC_DRIVER);
        try {
            Class.forName(driverClass);
        }
        catch(ClassNotFoundException nfe) {
             //.. error handling omitted
        }
//        InitialContext context = new InitialContext();
//        DataSource datasource = (DataSource) context.lookup("java:jboss/datasources/CustomKeycloakDS");
//        return datasource.getConnection();

        return DriverManager.getConnection(
                config.get(CONFIG_KEY_JDBC_URL),
                config.get(CONFIG_KEY_DB_USERNAME),
                config.get(CONFIG_KEY_DB_PASSWORD));
    }
}
