package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IbanBulkOperationRequest {

    @JsonProperty("operations")
    @NotNull(message = "Operations list cannot be null")
    @NotEmpty(message = "Operations list cannot be empty")
    @Size(min = 1, max = 100, message = "At least 1 and maximum 100 operations allowed per request")
    @Valid
    private List<IbanOperation> operations;
}