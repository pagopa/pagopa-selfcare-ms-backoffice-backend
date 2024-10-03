package it.pagopa.selfcare.pagopa.backoffice.model.connector.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Data
public class WrapperStation {

    @JsonProperty(required = true)
    @NotBlank
    private String stationCode;

    @JsonProperty(required = true)
    private Boolean enabled;

    private String brokerDescription;

    @JsonProperty(required = true)
    @NotNull
    private Long version;


    @JsonProperty(required = true)
    @NotNull
    private Integer associatedCreditorInstitutions;

    private Instant activationDate;

    private Instant createdAt;

    private Instant modifiedAt;

    @JsonProperty(required = true)
    @NotNull
    private WrapperStatus wrapperStatus;

    private Boolean flagStandin;

    private Boolean isConnectionSync;
}
