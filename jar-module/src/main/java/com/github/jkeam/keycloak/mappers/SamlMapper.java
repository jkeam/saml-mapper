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
    public static final String ATTRIBUTE_NAME = "attribute.name";
    public static final String ATTRIBUTE_FRIENDLY_NAME = "attribute.friendly.name";
    public static final String ATTRIBUTE_VALUE = "attribute.value";

    /*
    static {
        ProviderConfigProperty property;
        property = new ProviderConfigProperty();
        property.setName(ATTRIBUTE_NAME);
        property.setLabel("Attribute Name");
        property.setHelpText("Name of attribute to search for in assertion.  You can leave this blank and specify a friendly name instead.");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(property);
        property = new ProviderConfigProperty();
        property.setName(ATTRIBUTE_FRIENDLY_NAME);
        property.setLabel("Friendly Name");
        property.setHelpText("Friendly name of attribute to search for in assertion.  You can leave this blank and specify a name instead.");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(property);
        property = new ProviderConfigProperty();
        property.setName(ATTRIBUTE_VALUE);
        property.setLabel("Attribute Value");
        property.setHelpText("Value the attribute must have.  If the attribute is a list, then the value must be contained in the list.");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(property);
        property = new ProviderConfigProperty();
        property.setName("group");
        property.setLabel("Group");
        property.setHelpText("Group to grant to user. i.e. /Group1/SubGroup2");
        property.setType(ProviderConfigProperty.STRING_TYPE);
        configProperties.add(property);
    }
   */

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
//        String groupName = mapperModel.getConfig().get("group");
//        if (isAttributePresent(mapperModel, context)) {
//            GroupModel group = KeycloakModelUtils.findGroupByPath(realm, groupName);
//            if (group == null) throw new IdentityBrokerException("Unable to find group: " + groupName);
//            user.joinGroup(group);
//        }
        user.setSingleAttribute("enriched", "yes");
        logger.info(user);
    }

    @Override
    public void updateBrokeredUserLegacy(KeycloakSession keycloakSession, RealmModel realmModel, UserModel userModel, IdentityProviderMapperModel identityProviderMapperModel, BrokeredIdentityContext brokeredIdentityContext) {
        logger.info("updateBrokeredUserLegacy");
        logger.info(userModel);
    }

    @Override
    public void updateBrokeredUser(KeycloakSession session, RealmModel realm, UserModel user, IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        logger.info("updateBrokeredUser");
//        String groupName = mapperModel.getConfig().get("group");
//        GroupModel group = KeycloakModelUtils.findGroupByPath(realm, groupName);
//        if (group == null) throw new IdentityBrokerException("Unable to find group: " + groupName);
//        if (!isAttributePresent(mapperModel, context)) {
//            user.leaveGroup(group);ProviderConfigProperty
//        } else {
//            user.joinGroup(group);
//        }
        user.setSingleAttribute("enriched", "yes");
        logger.info(user);
    }

    protected boolean isAttributePresent(IdentityProviderMapperModel mapperModel, BrokeredIdentityContext context) {
        logger.trace("isAttributePresent");
        String name = mapperModel.getConfig().get(ATTRIBUTE_NAME);
        if (name != null && name.trim().equals("")) name = null;
        String friendly = mapperModel.getConfig().get(ATTRIBUTE_FRIENDLY_NAME);
        if (friendly != null && friendly.trim().equals("")) friendly = null;
        String desiredValue = mapperModel.getConfig().get(ATTRIBUTE_VALUE);
        AssertionType assertion = (AssertionType)context.getContextData().get(SAMLEndpoint.SAML_ASSERTION);
        for (AttributeStatementType statement : assertion.getAttributeStatements()) {
            for (AttributeStatementType.ASTChoiceType choice : statement.getAttributes()) {
                AttributeType attr = choice.getAttribute();
                if (name != null && !name.equals(attr.getName())) continue;
                if (friendly != null && !friendly.equals(attr.getFriendlyName())) continue;
                for (Object val : attr.getAttributeValue()) {
                    if (val.equals(desiredValue)) return true;
                }
            }
        }
        return false;
    }
}
