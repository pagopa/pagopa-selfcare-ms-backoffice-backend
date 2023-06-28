package it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.List;

@Data
public class IbanEnhanced {

    @JsonProperty("ci_owner")
    private String ecOwner;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("due_date")
    private OffsetDateTime dueDate;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("labels")
    private List<IbanLabel> labels;

    @JsonProperty("validity_date")
    private OffsetDateTime validityDate;

    @JsonProperty("publication_date")
    private OffsetDateTime publicationDate;
}
