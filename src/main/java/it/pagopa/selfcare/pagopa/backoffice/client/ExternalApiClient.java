package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.ExternalFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Delegation;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Institution;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.client.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.model.institutions.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "external-api", url = "${rest-client.external-api.base-url}", configuration = ExternalFeignConfig.class)
public interface ExternalApiClient {

    @GetMapping(value = "/institutions/{institutionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Institution getInstitution(@PathVariable(value = "institutionId") String id);

    @GetMapping(value = "/institutions", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<InstitutionInfo> getInstitutions(@RequestParam(value = "userIdForAuth") String userIdForAuth);

    @GetMapping(value = "/institutions/{institutionId}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<Product> getInstitutionUserProducts(@PathVariable(value = "institutionId") String institutionId, @RequestParam(value = "userId") String userId);

    @GetMapping(value = "/delegations", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<Delegation> getBrokerDelegation(@RequestParam String institutionId, @RequestParam String brokerId, @RequestParam String productId, @RequestParam String mode);


}
