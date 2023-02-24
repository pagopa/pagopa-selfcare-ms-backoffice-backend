package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.InstitutionInfo;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.product.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "${rest-client.external-api.serviceCode}", url = "${rest-client.external-api.base-url}")
public interface ExternalApiRestClient extends ExternalApiConnector {

    @GetMapping(value = "${rest-client.external-api.getInstitution.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Institution getInstitution(@PathVariable(value = "institutionId") String id);
    
    @GetMapping(value = "${rest-client.external-api.getInstitutions.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<InstitutionInfo> getInstitutions(@RequestParam(value = "userIdForAuth")String userIdForAuth);
    
    @GetMapping(value = "${rest-client.external-api.getInstitutionUserProducts.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<Product> getInstitutionUserProducts(@PathVariable(value = "institutionId")String institutionType);

}
