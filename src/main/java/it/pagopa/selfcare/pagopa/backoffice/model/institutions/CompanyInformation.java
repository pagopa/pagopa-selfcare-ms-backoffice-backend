package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInformation {

    @ApiModelProperty(value = "Institution's REA")
    private String rea;

    @ApiModelProperty(value = "Institution's share capital value")
    @JsonProperty(value = "share_capital")
    private String shareCapital;

    @ApiModelProperty(value = "Institution's business register place")
    @JsonProperty(value = "business_register_place")
    private String businessRegisterPlace;

}
