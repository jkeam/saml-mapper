package com.github.jkeam.keycloak.storageproviders;

import org.keycloak.component.ComponentModel;
import org.keycloak.component.ComponentValidationException;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.UserStorageProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import static com.github.jkeam.keycloak.storageproviders.CustomUserStorageProviderConstants.*;

/**
 * CustomUser storage provider factory.
 *
 * @author jkeam
 */
public class CustomUserStorageProviderFactory implements UserStorageProviderFactory<CustomUserStorageProvider> {
    private static final Logger log = LoggerFactory.getLogger(CustomUserStorageProviderFactory.class);
    protected final List<ProviderConfigProperty> configMetadata;

    public CustomUserStorageProviderFactory() {
        log.info("CustomUserStorageProviderFactory created");

        // Create config metadata
        configMetadata = ProviderConfigurationBuilder.create()
                .property()
                .name(CONFIG_KEY_JDBC_DRIVER)
                .label("JDBC Driver Class")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("com.mysql.jdbc.Driver")
                .helpText("Fully qualified class name of the JDBC driver")
                .add()
                .property()
                .name(CONFIG_KEY_JDBC_URL)
                .label("JDBC URL")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("jdbc:mysql://192.168.1.11:3306/custom_keycloak")
                .helpText("JDBC URL used to connect to the user database")
                .add()
                .property()
                .name(CONFIG_KEY_DB_USERNAME)
                .label("Database User")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("root")
                .helpText("Username used to connect to the database")
                .add()
                .property()
                .name(CONFIG_KEY_DB_PASSWORD)
                .label("Database Password")
                .type(ProviderConfigProperty.STRING_TYPE)
                .defaultValue("root")
                .helpText("Password used to connect to the database")
                .secret(true)
                .add()
                .property()
                .name(CONFIG_KEY_VALIDATION_QUERY)
                .label("SQL Validation Query")
                .type(ProviderConfigProperty.STRING_TYPE)
                .helpText("SQL query used to validate a connection")
                .defaultValue("select 1")
                .add()
                .build();
    }

    @Override
    public CustomUserStorageProvider create(KeycloakSession keycloakSession, ComponentModel model) {
        log.info("creating new CustomUserStorageProvider");
        return new CustomUserStorageProvider(keycloakSession,model);
    }

    @Override
    public String getId() {
        log.info("getId()");
        return "custom-user-provider";
    }

    // Configuration support methods
    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configMetadata;
    }

    @Override
    public void validateConfiguration(KeycloakSession session, RealmModel realm, ComponentModel config) throws ComponentValidationException {

        try (Connection c = DbUtil.getConnection(config)) {
            log.info("Testing connection..." );
            c.createStatement().execute(config.get(CONFIG_KEY_VALIDATION_QUERY));
            log.info("Connection OK!" );
        }
        catch(Exception ex) {
            log.warn(ex.toString());
            log.warn("Unable to validate connection: ex={}", ex.getMessage());
            log.warn(Arrays.toString(ex.getStackTrace()));
            throw new ComponentValidationException("Unable to validate database connection",ex);
        }
    }

    @Override
    public void onUpdate(KeycloakSession session, RealmModel realm, ComponentModel oldModel, ComponentModel newModel) {
        log.info("onUpdate()" );
    }

    @Override
    public void onCreate(KeycloakSession session, RealmModel realm, ComponentModel model) {
        log.info("onCreate()" );
    }
}