package it.pagopa.selfcare.pagopa.backoffice.model.connector.creditorinstitution;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Creditor Institutions
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class BrokerCreditorInstitutionDetails {

    @JsonProperty("creditor_institutions")
    @NotNull
    @Schema(description = "List of creditor institutions associated to the same broker by different stations")
    private List<CreditorInstitutionDetail> creditorInstitutions;

    @JsonProperty("page_info")
    @Schema()
    @NotNull
    @Valid
    private PageInfo pageInfo;
}
