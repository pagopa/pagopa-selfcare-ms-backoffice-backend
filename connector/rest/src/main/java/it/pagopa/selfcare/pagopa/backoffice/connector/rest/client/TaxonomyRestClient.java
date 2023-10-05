package it.pagopa.selfcare.pagopa.backoffice.connector.rest.client;

import it.pagopa.selfcare.pagopa.backoffice.connector.api.TaxonomyConnector;
import it.pagopa.selfcare.pagopa.backoffice.connector.model.taxonomy.Taxonomy;
import it.pagopa.selfcare.pagopa.backoffice.connector.rest.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "${rest-client.taxonomy.serviceCode}", url = "${rest-client.taxonomy.base-url}", configuration = FeignConfig.class)
public interface TaxonomyRestClient extends TaxonomyConnector {

    @GetMapping(value = "${rest-client.taxonomy.getTaxonomies.path}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<Taxonomy> getTaxonomies();
}
