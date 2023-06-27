package it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.OffsetDateTimeSerializer;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
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
    private LocalDateTime dueDate;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("is_active")
    private boolean isActive;

    @JsonProperty("labels")
    private List<IbanLabel> labels;

    @JsonProperty("validity_date")
    private LocalDateTime validityDate;

    @JsonProperty("publication_date")
    private LocalDateTime publicationDate;
}
