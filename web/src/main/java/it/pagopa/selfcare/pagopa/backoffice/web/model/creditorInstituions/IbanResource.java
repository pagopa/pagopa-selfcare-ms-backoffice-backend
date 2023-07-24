package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
public class IbanResource {


    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.description}")
    private String description;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.due-date}", required = true)
    private OffsetDateTime dueDate;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.iban}", required = true)
    private String iban;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.isActive}", required = true)
    private boolean active;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.labels}")
    private List<IbanLabel> labels;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.ecOwner}", required = true)
    private String ecOwner;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.companyName}")
    private String companyName;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.validityDate}", required = true)
    private OffsetDateTime validityDate;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.publicationDate}", required = true)
    private OffsetDateTime publicationDate;
}
