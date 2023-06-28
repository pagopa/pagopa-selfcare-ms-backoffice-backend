package it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IbanLabel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}
