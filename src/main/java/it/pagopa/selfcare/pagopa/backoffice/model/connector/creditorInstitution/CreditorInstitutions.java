package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorInstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class CreditorInstitutions {

    @JsonProperty("creditor_institutions")
    private List<CreditorInstitution> creditorInstitutionList;
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
