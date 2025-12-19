package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanOperation {

    @JsonProperty("operation")
    @NotNull(message = "Operation type is required")
    private IbanOperationType type;

    @JsonProperty("ibanValue")
    @NotNull(message = "IBAN value is required")
    private String ibanValue;

    @JsonProperty("description")
    private String description;

    @JsonProperty("validityDate")
    @NotNull(message = "validityDate value is required")
    private String validityDate;

    @JsonProperty("creditorInstitutionCode")
    @NotNull(message = "Creditor institution code is required")
    private String creditorInstitutionCode;

}
