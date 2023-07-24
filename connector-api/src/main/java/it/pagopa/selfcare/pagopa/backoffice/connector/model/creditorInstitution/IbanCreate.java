package it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class IbanCreate {

    @JsonProperty("description")
    private String description;

    @JsonProperty("due_date")
    private OffsetDateTime dueDate;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("is_active")
    private boolean active;

    @JsonProperty("labels")
    private List<IbanLabel> labels;

    @JsonProperty("validity_date")
    private OffsetDateTime validityDate;
}
