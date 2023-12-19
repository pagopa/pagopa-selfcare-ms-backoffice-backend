package it.pagopa.selfcare.pagopa.backoffice.model.iban;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IbanLabel {

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;
}
