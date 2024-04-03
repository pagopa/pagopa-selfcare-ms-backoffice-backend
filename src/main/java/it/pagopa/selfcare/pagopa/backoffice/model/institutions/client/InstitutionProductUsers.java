package it.pagopa.selfcare.pagopa.backoffice.model.institutions.client;

import lombok.Data;

import java.util.List;

/**
 * Model that hold institution's users info
 */
@Data
public class InstitutionProductUsers {

    private String id;
    private String email;
    private String name;
    private String surname;
    private String fiscalCode;
    private List<String> roles;
}
