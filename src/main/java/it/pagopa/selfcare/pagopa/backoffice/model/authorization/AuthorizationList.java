package it.pagopa.selfcare.pagopa.backoffice.model.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.pagopa.selfcare.pagopa.backoffice.model.connector.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that represent a paginated list of {@link Authorization}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthorizationList {

    private List<Authorization> authorizations;

    @JsonProperty("page_info")
    private PageInfo pageInfo;
}
