package it.pagopa.selfcare.pagopa.backoffice.model.commissionbundle;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BundlePaymentType {

    @ApiModelProperty(value = "The payment type unique identifier", required = true)
    private String id;

    @ApiModelProperty(value = "A user-friendly name for the payment type", required = true)
    private String name;

    @ApiModelProperty(value = "The description related to the payment type", required = true)
    private String description;

    @ApiModelProperty(value = "The date on which the payment type was created", required = true)
    @JsonProperty("created_date")
    private LocalDateTime createdDate;
}
