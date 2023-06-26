package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class IbanCreateRequestDto {

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.code}", required = true)
    @Size(max = 30)
    @JsonProperty(required = true)
    @NotBlank
    private String creditorInstitutionCode;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.description}", required = true)
    private String description;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.due-date}", required = true)
    private LocalDateTime dueDate;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.iban}", required = true)
    private String iban;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.isActive}", required = true)
    private boolean isActive;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.labels}", required = true)
    private List<IbanLabel> labels;

    @ApiModelProperty(value = "${swagger.api.creditor-institutions.ibans.validityDate}", required = true)
    private LocalDateTime validityDate;

}
