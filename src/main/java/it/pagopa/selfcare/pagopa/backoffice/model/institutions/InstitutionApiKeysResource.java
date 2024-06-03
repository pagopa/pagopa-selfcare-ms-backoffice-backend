package it.pagopa.selfcare.pagopa.backoffice.model.institutions;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionApiKeys;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InstitutionApiKeysResource {

    @JsonProperty("institution_api_key_list")
    private List<@Valid InstitutionApiKeys> institutionApiKeys;
}