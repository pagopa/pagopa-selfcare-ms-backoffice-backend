package it.pagopa.selfcare.pagopa.backoffice.model.stations;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class that contains the operator review on a station
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperatorStationReview {

    @Schema(description = "Operator review note")
    private String note;
}
