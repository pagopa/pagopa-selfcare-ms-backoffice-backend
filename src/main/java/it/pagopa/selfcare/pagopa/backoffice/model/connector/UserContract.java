package it.pagopa.selfcare.pagopa.backoffice.model.connector;

import lombok.Data;

@Data
public class UserContract {
    private String id;
    private String name;
    private String fullName;
    private String taxCode;
    private String email;
}
