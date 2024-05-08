package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInformation {

    @Schema(description = "Institution's REA")
    private String rea;

    @Schema(description = "Institution's share capital value")
    @JsonProperty(value = "share_capital")
    private String shareCapital;

    @Schema(description = "Institution's business register place")
    @JsonProperty(value = "business_register_place")
    private String businessRegisterPlace;

}
