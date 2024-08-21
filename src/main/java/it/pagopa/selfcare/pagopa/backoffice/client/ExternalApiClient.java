package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.ExternalFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.DelegationExternal;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Product;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionProductUsers;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.Institutions;
import it.pagopa.selfcare.pagopa.backoffice.model.users.client.UserInstitution;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@FeignClient(name = "external-api", url = "${rest-client.external-api.base-url}",
        configuration = ExternalFeignConfig.class)
@Validated
public interface ExternalApiClient {

    @GetMapping(value = "/institutions/{institutionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    Institution getInstitution(@PathVariable(value = "institutionId") String id);


    @GetMapping(value = "/institutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    Institutions getInstitutionsFiltered(@RequestParam(value = "taxCode") String institutionTaxCode);

    @GetMapping(value = "/institutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    List<InstitutionInfo> getInstitutions(@RequestParam(value = "userIdForAuth") String userIdForAuth);

    @GetMapping(value = "/institutions/{institutionId}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    List<Product> getInstitutionUserProducts(@PathVariable(value = "institutionId") String institutionId,
                                             @RequestParam(value = "userId") String userId);

    @GetMapping(value = "/institutions/{institution-id}/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    List<InstitutionProductUsers> getInstitutionProductUsers(
            @PathVariable(value = "institution-id") String id,
            @RequestParam(required = false) String userIdForAuth,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) List<String> productRoles
    );

    @GetMapping(value = "/delegations", produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(cacheNames = "getBrokerDelegation")
    @ResponseBody
    @Valid
    List<DelegationExternal> getBrokerDelegation(
            @RequestParam String institutionId,
            @RequestParam String brokerId,
            @RequestParam String productId,
            @RequestParam String mode,
            @RequestParam String search
    );

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Valid
    List<UserInstitution> getUserInstitution(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String institutionId,
            @RequestParam(required = false) List<String> productRoles,
            @RequestParam(required = false) List<String> roles,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    );

}
