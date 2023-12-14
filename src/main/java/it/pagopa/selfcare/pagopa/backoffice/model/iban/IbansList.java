package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Ibans
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IbansList {

    @JsonProperty("ibans")
    @NotNull
    @Schema(description = "List of IBANs associated to the passed creditor institutions")
    private List<IbanDetails> ibans;

    @JsonProperty("page_info")
    @Schema()
    @NotNull
    @Valid
    private PageInfo pageInfo;
}
