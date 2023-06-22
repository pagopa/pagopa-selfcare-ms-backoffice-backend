package it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class IbansDetails {

    @JsonProperty("ibans")
    @NotNull
    private List<IbanDetails> ibanList;
}
