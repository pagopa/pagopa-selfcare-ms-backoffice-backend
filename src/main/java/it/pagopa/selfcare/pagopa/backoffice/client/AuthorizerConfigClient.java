package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.AuthorizerConfigFeignConfigImpl;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.Authorization;
import it.pagopa.selfcare.pagopa.backoffice.model.authorization.AuthorizationList;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Client for Authorizer Config service
 */
@FeignClient(name = "authorizer-config", url = "${rest-client.authorizer-config.base-url}", configuration = AuthorizerConfigFeignConfigImpl.class)
public interface AuthorizerConfigClient {

    @GetMapping(value = "/authorizations/{authorizationId}", produces = MediaType.APPLICATION_JSON_VALUE)
    AuthorizationList getAuthorization(@PathVariable("authorization-id") String authorizationId);

    @PostMapping(value = "/authorizations", produces = MediaType.APPLICATION_JSON_VALUE)
    AuthorizationList createAuthorization(@RequestBody Authorization authorization);

    @PutMapping(value = "/authorizations/{authorization-id}", produces = MediaType.APPLICATION_JSON_VALUE)
    AuthorizationList updateAuthorization(
            @PathVariable("authorization-id") String authorizationId,
            @RequestBody Authorization authorization);
}
