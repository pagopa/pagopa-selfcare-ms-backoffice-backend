package it.pagopa.selfcare.pagopa.backoffice.client;

import it.pagopa.selfcare.pagopa.backoffice.config.feign.TaxonomyFeignConfig;
import it.pagopa.selfcare.pagopa.backoffice.model.taxonomies.client.TaxonomyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@FeignClient(name = "taxonomy", url = "${rest-client.taxonomy.base-url}", configuration = TaxonomyFeignConfig.class)
public interface TaxonomyClient {

    @GetMapping(value = "/taxonomy", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    List<TaxonomyDTO> getTaxonomies();

}
