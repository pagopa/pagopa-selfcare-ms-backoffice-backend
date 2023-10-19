package it.pagopa.selfcare.pagopa.backoffice.web.model.creditorinstituions;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class BrokerEcDto {

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.brokerCode}", required = true)
    @Size(max = 30)
    @JsonProperty
    @NotBlank
    private String brokerCode;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.enabled}", required = true)
    @JsonProperty
    private Boolean enabled;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.brokerCode}", required = true)
    @Size(max = 30)
    @JsonProperty
    private String description;

    @ApiModelProperty(value = "${swagger.creditor-institutions.model.extendedFaultBean}", required = true)
    @JsonProperty
    private Boolean extendedFaultBean;
}
