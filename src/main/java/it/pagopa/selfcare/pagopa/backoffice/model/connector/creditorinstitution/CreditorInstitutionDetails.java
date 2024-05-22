package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CreditorInstitutionDetails extends CreditorInstitution {
    @JsonProperty("address")
    private CreditorInstitutionAddress address;

    @JsonProperty("psp_payment")
    private Boolean pspPayment;

    @JsonProperty("reporting_ftp")
    private Boolean reportingFtp;

    @JsonProperty("reporting_zip")
    private Boolean reportingZip;
}
