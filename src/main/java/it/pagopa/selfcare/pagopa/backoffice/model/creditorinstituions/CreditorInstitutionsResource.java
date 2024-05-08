package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class CreditorInstitutionsResource {

    @Schema(description = "A list of Creditor Institution's",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("creditor_institutions")
    private List<CreditorInstitutionResource> creditorInstitutionList;

    @Schema(description = "info pageable",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
