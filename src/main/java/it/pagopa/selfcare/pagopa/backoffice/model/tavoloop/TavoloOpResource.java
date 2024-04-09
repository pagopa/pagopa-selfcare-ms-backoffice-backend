package it.pagopa.selfcare.pagopa.backoffice.model.tavoloop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TavoloOpResource {


    @ApiModelProperty(value = "Fiscal code", required = true)
    @JsonProperty(required = true)
    private String taxCode;

    @ApiModelProperty(value = "Psp", required = true)
    @JsonProperty(required = true)
    private String name;

    @ApiModelProperty(value = "referent", required = true)
    @JsonProperty(required = true)
    private String referent;

    @ApiModelProperty(value = "contact person's email address", required = true)
    @JsonProperty(required = true)
    private String email;

    @ApiModelProperty(required = true)
    @JsonProperty(required = true)
    private String telephone;

    @ApiModelProperty(value = "Date of update", required = true)
    @JsonProperty(required = true)
    private Instant modifiedAt;

    @ApiModelProperty(value = "person who made the change", required = true)
    @JsonProperty(required = true)
    private String modifiedBy;

    @ApiModelProperty(value = "Date of insert")
    private Instant createdAt;

    @ApiModelProperty(value = "Person who made the change", required = true)
    @JsonProperty(required = true)
    private String createdBy;

}
