package it.pagopa.selfcare.pagopa.backoffice.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.AuthenticatedPrincipal;

@Getter
@ToString
@EqualsAndHashCode(of = "id")
public class SelfCareUser implements AuthenticatedPrincipal {
    private final String id;
    private String email;
    private String surname;
    private String userName;

    private String orgVat;

    public SelfCareUser(String id) {
        this.id = id;
    }

    public static SelfCareUserBuilder builder(String id) {
        return new SelfCareUserBuilder(id);
    }

    @Override
    public String getName() {
        return id;
    }

    public static class SelfCareUserBuilder {

        private final String id;
        private String email;
        private String name;
        private String surname;
        private String orgVat;

        private SelfCareUserBuilder(String id) {
            this.id = id;
        }

        public SelfCareUserBuilder email(String email) {
            this.email = email;
            return this;
        }

        public SelfCareUserBuilder name(String name) {
            this.name = name;
            return this;
        }

        public SelfCareUserBuilder surname(String surname) {
            this.surname = surname;
            return this;
        }

        public SelfCareUserBuilder orgVat(String orgVat) {
            this.orgVat = orgVat;
            return this;
        }

        public SelfCareUser build() {
            SelfCareUser user = new SelfCareUser(id);
            user.email = email;
            user.userName = name;
            user.surname = surname;
            user.orgVat = orgVat;
            return user;
        }

    }
}
