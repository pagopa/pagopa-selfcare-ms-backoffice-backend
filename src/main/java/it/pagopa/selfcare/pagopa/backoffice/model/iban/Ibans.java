package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ibans {

    @Schema(description = "Creditor Institution's IBAN objects",requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("ibans_enhanced")
    @NotNull
    private List<Iban> ibanList;
}
