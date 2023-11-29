package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper.WrapperStatus;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class StationResource {

    @ApiModelProperty(value = "Station's unique identifier", required = true)
    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;
    @ApiModelProperty(value = "Station's activation state", required = true)
    @JsonProperty(required = true)
    private Boolean enabled;
    @ApiModelProperty(value = "Station broker's description")
    private String brokerDescription;
    @ApiModelProperty("Station's version")
    @JsonProperty(required = true)
    @NotNull
    private Long version;
    @ApiModelProperty(value = "Station's status")
    private WrapperStatus wrapperStatus;
    @ApiModelProperty("Number of station's creditor institutions")
    @JsonProperty(required = true)
    @NotNull
    private Integer associatedCreditorInstitutions;
    @ApiModelProperty("Station's activation date")
    private Instant activationDate;
    @ApiModelProperty("Station created on")
    private Instant createdAt;
    @ApiModelProperty("Station's last modified date")
    private Instant modifiedAt;

}
