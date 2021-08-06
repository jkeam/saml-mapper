package com.github.jkeam.keycloak.storageproviders;

import org.keycloak.component.ComponentModel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.github.jkeam.keycloak.storageproviders.CustomUserStorageProviderConstants.*;

/**
 * Database Utility
 *
 * @author jkeam
 */
public class DbUtil {
    public static Connection getConnection(ComponentModel config) throws SQLException, ClassNotFoundException {
        String driverClass = config.get(CONFIG_KEY_JDBC_DRIVER);
        Class.forName(driverClass);
        return DriverManager.getConnection(
                config.get(CONFIG_KEY_JDBC_URL),
                config.get(CONFIG_KEY_DB_USERNAME),
                config.get(CONFIG_KEY_DB_PASSWORD));
    }
}
