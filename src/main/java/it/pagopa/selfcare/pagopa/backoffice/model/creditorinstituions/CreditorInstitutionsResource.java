package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class CreditorInstitutionsResource {

    @ApiModelProperty(value = "A list of Creditor Institution's", required = true)
    @JsonProperty("creditor_institutions")
    private List<CreditorInstitutionResource> creditorInstitutionList;

    @ApiModelProperty(value = "info pageable", required = true)
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
