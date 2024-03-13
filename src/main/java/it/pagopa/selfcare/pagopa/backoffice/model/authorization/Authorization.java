package it.pagopa.selfcare.pagopa.backoffice.model.authorization;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model that represent an authorization
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Authorization {

    private String id;

    private String domain;

    @JsonProperty("subscription_key")
    private String subscriptionKey;

    private String description;

    private AuthorizationOwner owner;

    @JsonProperty("authorized_entities")
    private List<AuthorizationEntity> authorizedEntities;

    @JsonProperty("other_metadata")
    private List<AuthorizationMetadata> otherMetadata;

    @JsonProperty("inserted_at")
    private String insertedAt;

    @JsonProperty("last_update")
    private String lastUpdate;

    @JsonProperty("last_forced_refresh")
    private String lastForcedRefresh;

}
