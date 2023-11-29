package it.pagopa.selfcare.pagopa.backoffice.model.connector.station;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * Stations
 */
@Data
public class CreditorInstitutionStation extends Station {
  @Min(0)
  @JsonProperty("application_code")
  private Long applicationCode;

  @JsonProperty("aux_digit")
  private Long auxDigit;

  @Min(0)
  @JsonProperty("segregation_code")
  private Long segregationCode;

  @JsonProperty("mod4")
  private Boolean mod4;

  @JsonProperty("broadcast")
  private Boolean broadcast;
}
