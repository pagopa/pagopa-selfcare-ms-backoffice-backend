package it.pagopa.selfcare.pagopa.backoffice.connector.model.creditorInstitution;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class IbansEnhanced {

    @JsonProperty("ibans_enhanced")
    @NotNull
    private List<IbanEnhanced> ibanList;
}
