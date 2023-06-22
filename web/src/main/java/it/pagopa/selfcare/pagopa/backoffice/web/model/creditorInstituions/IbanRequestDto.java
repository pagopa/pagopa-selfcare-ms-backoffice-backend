package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorInstituions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class IbanRequestDto {

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.code}", required = true)
    @Size(max = 30)
    @JsonProperty(required = true)
    @NotBlank
    private String creditorInstitutionCode;
}
