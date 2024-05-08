package it.pagopa.selfcare.pagopa.backoffice.model.creditorinstituions;

import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.stations.StationResource;
import lombok.Data;

@Data
public class CreditorInstitutionStationResource extends StationResource {
    @Schema(description = "Station's application code")
    private Long applicationCode;
    @Schema(description = "Station's auxiliary digit")
    private Long auxDigit;
    @Schema(description = "Station's segregation code number")
    private Long segregationCode;
    @Schema(description = "Station's mod 4 enabled")
    private Boolean mod4;
    @Schema(description = "Station's broadcast enabled")
    private Boolean broadcast;
}
