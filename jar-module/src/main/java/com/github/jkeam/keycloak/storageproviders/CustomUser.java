package com.github.jkeam.keycloak.storageproviders;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.*;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Custom user to save in the database.
 *
 * @author jkeam
 */
class CustomUser extends AbstractUserAdapter {
    private static final Logger log = LoggerFactory.getLogger(CustomUser.class);
    public static final String BIRTHDAY_FORMAT = "yyyy-MM-dd";
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String enriched = "no";
    private Boolean enabled;
    private Boolean emailVerified;

    private CustomUser(KeycloakSession session, RealmModel realm,
                       ComponentModel storageProviderModel,
                       String username,
                       String email,
                       String firstName,
                       String lastName,
                       Date birthDate,
                       String enriched,
                       Boolean enabled,
                       Boolean emailVerified) {
        super(session, realm, storageProviderModel);
        this.username = username;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.enriched = enriched;
        this.enabled = enabled;
        this.emailVerified = emailVerified;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    @Override
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getEnriched() {
        return enriched;
    }

    public void setEnriched(String enriched) {
        this.enriched = enriched;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        log.info("setSingleAttribute {}, {}", new Object[]{name, value});
        String field;
        switch(name) {
            case UserModel.USERNAME:
                this.username = value;
                field = UserModel.USERNAME;
                break;
            case UserModel.FIRST_NAME:
                this.firstName = value;
                field = UserModel.FIRST_NAME;
                break;
            case UserModel.LAST_NAME:
                this.lastName = value;
                field = UserModel.LAST_NAME;
                break;
            case UserModel.EMAIL:
                this.email = value;
                field = UserModel.EMAIL;
                break;
            case "birthDate":
                DateFormat fmt = new SimpleDateFormat(BIRTHDAY_FORMAT);
                field = "birthDate";
                try {
                    this.birthDate = fmt.parse(value);
                } catch(ParseException pe) {
                    log.error("ParseException {}", pe.getMessage());
                }
                break;
            case "enriched":
                field = "enriched";
                this.enriched = value;
                break;
            case UserModel.ENABLED:
                field = UserModel.ENABLED;
                this.setEnabled(Boolean.parseBoolean(value));
                break;
            default:
                field = name;
        }
        try ( Connection c = DbUtil.getConnection(this.storageProviderModel)) {
            PreparedStatement st = c.prepareStatement(String.format("update users set %s = ? where username = ?", field));
            st.setString(1, value);
            st.setString(2, username);
            int row = st.executeUpdate();
            log.info("Updated {} rows", row);
        }
        catch(Exception ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }

    @Override
    public void removeAttribute(String name) {
        String field;
        switch(name) {
            case UserModel.USERNAME:
                field = UserModel.USERNAME;
                this.username = "";
                break;
            case UserModel.FIRST_NAME:
                field = UserModel.FIRST_NAME;
                this.firstName = "";
                break;
            case UserModel.LAST_NAME:
                field = UserModel.LAST_NAME;
                this.lastName = "";
                break;
            case UserModel.EMAIL:
                field = UserModel.EMAIL;
                this.email = "";
                break;
            case "birthDate":
                field = "birthDate";
                this.birthDate = null;
                break;
            case "enriched":
                field = "enriched";
                this.enriched = "no";
                break;
            case UserModel.ENABLED:
                field = UserModel.ENABLED;
                this.setEnabled(false);
                break;
            default:
                field = name;
        }
        try ( Connection c = DbUtil.getConnection(this.storageProviderModel)) {
            PreparedStatement st = c.prepareStatement(String.format("update users set %s = '' where username = ?", field));
            st.setString(1, username);
            int row = st.executeUpdate();
            log.info("Updated {} rows", row);
        }
        catch(Exception ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        setSingleAttribute(name, values.get(0));
    }

    @Override
    public String getFirstAttribute(String name) {
        switch(name) {
            case UserModel.USERNAME:
                return name;
            case UserModel.FIRST_NAME:
                return firstName;
            case UserModel.LAST_NAME:
                return lastName;
            case UserModel.EMAIL:
                return email;
            case UserModel.ENABLED:
                if (enabled != null) {
                    return Boolean.toString(enabled);
                }
                return "";
            case "birthDate":
                DateFormat fmt = new SimpleDateFormat(BIRTHDAY_FORMAT);
                return fmt.format(birthDate);
            case "enriched":
                return enriched;
            case UserModel.EMAIL_VERIFIED:
                if (emailVerified != null) {
                    return Boolean.toString(emailVerified);
                }
                return "";
            default:
                return "";
        }
    }

    @Override
    public List<String> getAttribute(String name) {
        return Collections.singletonList(getFirstAttribute(name));
    }

    @Override
    public boolean isEmailVerified() {
        String val = this.getFirstAttribute(UserModel.EMAIL_VERIFIED);
        return Boolean.parseBoolean(val);
    }

    @Override
    public void setEmailVerified(boolean verified) {
        this.emailVerified = verified;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof UserModel) {
            UserModel that = (UserModel)o;
            return that.getId().equals(this.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.getId().hashCode();
    }

    @Override
    public void setFederationLink(String link) {
    }

    @Override
    public String getServiceAccountClientLink() {
        return null;
    }

    @Override
    public void setServiceAccountClientLink(String clientInternalId) {
    }

    @Override
    public void deleteRoleMapping(RoleModel role) {
    }

    @Override
    public void grantRole(RoleModel role) {
    }

    @Override
    public void joinGroup(GroupModel group) {
    }

    @Override
    public void leaveGroup(GroupModel group) {
    }

    @Override
    public void addRequiredAction(String action) {
    }

    @Override
    public void removeRequiredAction(String action) {
    }

    @Override
    public void addRequiredAction(RequiredAction action) {
    }

    @Override
    public void removeRequiredAction(RequiredAction action) {
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL, getEmail());
        attributes.add(UserModel.FIRST_NAME, getFirstName());
        attributes.add(UserModel.LAST_NAME, getLastName());
        if (getBirthDate() != null) {
            DateFormat fmt = new SimpleDateFormat(BIRTHDAY_FORMAT);
            attributes.add("birthDate", fmt.format(getBirthDate()));
        }
        attributes.add("enriched", getEnriched());
        attributes.add(UserModel.ENABLED, Boolean.toString(isEnabled()));
        attributes.add(UserModel.EMAIL_VERIFIED, Boolean.toString(isEmailVerified()));
        return attributes;
    }

    @Override
    public void setCreatedTimestamp(Long timestamp) {
        this.created = timestamp;
    }

    static class Builder {
        private final KeycloakSession session;
        private final RealmModel realm;
        private final ComponentModel storageProviderModel;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private Date birthDate;
        private String enriched;
        private Boolean enabled;
        private Boolean emailVerified;

        Builder(KeycloakSession session, RealmModel realm, ComponentModel storageProviderModel, String username) {
            this.session = session;
            this.realm = realm;
            this.storageProviderModel = storageProviderModel;
            this.username = username;
        }

        CustomUser.Builder username(String username) {
            this.username = username;
            return this;
        }

        CustomUser.Builder email(String email) {
            this.email = email;
            return this;
        }

        CustomUser.Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        CustomUser.Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        CustomUser.Builder birthDate(Date birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        CustomUser.Builder enriched(String enriched) {
            this.enriched = enriched;
            return this;
        }

        CustomUser.Builder enabled(Boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        CustomUser.Builder emailVerified(Boolean emailVerified) {
            this.emailVerified = emailVerified;
            return this;
        }

        CustomUser build() {
            return new CustomUser(
                    session,
                    realm,
                    storageProviderModel,
                    username,
                    email,
                    firstName,
                    lastName,
                    birthDate,
                    enriched,
                    enabled,
                    emailVerified);
        }
    }
}