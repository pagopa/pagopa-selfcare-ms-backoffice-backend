package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.PageInfo;
import lombok.Data;

import java.util.List;

@Data
public class CreditorInstitutionsResource {

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.list}", required = true)
    @JsonProperty("creditor_institutions")
    private List<CreditorInstitutionResource> creditorInstitutionList;

    @ApiModelProperty(value = "${swagger.model.pageinfo}", required = true)
    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
