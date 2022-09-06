package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.ExternalApiConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.institution.Institution;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(name = "${rest-client.external-api.serviceCode}", url = "${rest-client.external-api.base-url}")
public interface ExternalApiRestClient extends ExternalApiConnector {

    @GetMapping(value = "${rest-client.external-api.getInstitution.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    Institution getInstitution(@PathVariable(value = "id") String id);
    
}
