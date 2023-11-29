package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DpoData {

    @ApiModelProperty(value = "DPO's address", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String address;

    @ApiModelProperty(value = "DPO's PEC", required = true)
    @JsonProperty(required = true)
    @NotBlank
    @Email
    private String pec;

    @ApiModelProperty(value = "DPO's email", required = true)
    @JsonProperty(required = true)
    @NotBlank
    @Email
    private String email;

}
