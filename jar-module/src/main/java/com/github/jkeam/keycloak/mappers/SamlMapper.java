package com.github.jkeam.keycloak.mappers;

import org.jboss.logging.Logger;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityProviderMapper;
import org.keycloak.broker.saml.SAMLEndpoint;
import org.keycloak.broker.saml.SAMLIdentityProviderFactory;
import org.keycloak.dom.saml.v2.assertion.AssertionType;
import org.keycloak.dom.saml.v2.assertion.AttributeStatementType;
import org.keycloak.dom.saml.v2.assertion.AttributeType;
import org.keycloak.models.IdentityProviderMapperModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.provider.ProviderConfigProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper that enriches the user object after a saml assertion.
 *
 * @author jkeam
 */
public class SamlMapper extends AbstractProviderMapper implements IdentityProviderMapper {
    private static final Logger logger = Logger.getLogger(SamlMapper.class);
    private static final List<ProviderConfigProperty> configProperties = new ArrayList<>();

    public static final String PROVIDER_ID = "saml-mapper";
    public static final String[] COMPATIBLE_PROVIDERS = {SAMLIdentityProviderFactory.PROVIDER_ID};

    @Override
    public SamlMapper create(KeycloakSession session) {
        return new SamlMapper();
    }

    @Override
    public List<ProviderConfigProperty> getConfigProperties() {
        return configProperties;
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }

    @Override
    public String[] getCompatibleProviders() {
        return COMPATIBLE_PROVIDERS;
    }

    @Override
    public String getDisplayCategory() {
        return "Custom SAML Mapper";
    }

    @Override
    public String getDisplayType() {
        return "Custom SAML Attribute";
    }

    @Override
    public String getHelpText() {
        return "Custom enrichment.";
    }

    @Override
    public void importNewUser(KeycloakSession session, RealmModel realm, UserModel user, IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        logger.info("importNewUser");
        user.setSingleAttribute("enriched", "yes");
        logger.info(user);
    }

    @Override
    public void updateBrokeredUserLegacy(KeycloakSession keycloakSession, RealmModel realmModel, UserModel user, IdentityProviderMapperModel identityProviderMapperModel, BrokeredIdentityContext brokeredIdentityContext) {
        logger.info("updateBrokeredUserLegacy");
        user.setSingleAttribute("enriched", "yes");
        logger.info(user);
    }

    @Override
    public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user, IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        logger.info("updateBrokeredUser");
        user.setSingleAttribute("enriched", "yes");
        logger.info(user);
    }
}
