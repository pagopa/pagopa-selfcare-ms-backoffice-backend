package it.pagopa.selfcare.pagopa.backoffice.model.channels;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model class that contains the operator review on a channel
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OperatorChannelReview {

    @Schema(description = "Operator review note")
    private String note;
}
